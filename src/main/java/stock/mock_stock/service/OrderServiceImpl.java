package stock.mock_stock.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stock.mock_stock.common.StockNameCache;
import stock.mock_stock.dto.OrderResponseDto;
import stock.mock_stock.dto.StockInfoDto;
import stock.mock_stock.entity.*;
import stock.mock_stock.repository.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final AccountService accountService;
    private final OrderRepository orderRepository;
    private final PortfolioRepository portfolioRepository;
    private final AccountRepository accountRepository;
    private final OrderTransactionRepository orderTransactionRepository;
    private final UserRepository userRepository;
    private final StockDetailService stockDetailService;
    private final StockNameCache stockNameCache;

    @Override
    @Transactional
    public void processOrder(Long userId, String stockCode, Long orderQuantity, Long unitPrice, OrderType orderType, TradeActionType tradeActionType) {
        // TODO: 동시성 체크후 있다면 문제 해결할 것
        // TODO: 도메인 주도 설계 방식으로 수정 할것
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // NOTE: 해당 시세 조회
        if(orderType == OrderType.MARKET){
                unitPrice = stockDetailService.getStockInfo(stockCode).getStckCurPrice();
        }

        // NOTE: 주문 기록
        Order order = Order.builder()
                .user(user)
                .stckCode(stockCode)
                .stckOrdQty(orderQuantity)
                .stckOrdUnitPrice(unitPrice)
                .orderType(orderType)
                .tradeAction(tradeActionType)
                .orderStatus(OrderStatus.PENDING) // 주문 초기 상태
                .build();
        orderRepository.save(order); // 새 엔티티는 INSERT 실행 필요

        Account account = accountRepository.findByUserUid(userId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        // NOTE: 계좌 잔액 차감 (매수 시)
        if (tradeActionType == TradeActionType.BUY) {
            BigDecimal totalCost = BigDecimal.valueOf(unitPrice).multiply(BigDecimal.valueOf(orderQuantity));

            if (BigDecimal.valueOf(account.getBalance()).compareTo(totalCost) < 0) {
                throw new IllegalArgumentException("잔액 부족");
            }
            BigDecimal updatedBalance = BigDecimal.valueOf(account.getBalance()).subtract(totalCost);
            account.setBalance(updatedBalance.longValue()); // 이때 Dirty checking 적용됨, @Transactional 있다는 가정하에
//            accountRepository.save(account); // Dirty checking 으로 필요없음

            accountService.recordTransaction(account, -totalCost.longValue(), TransactionType.WITHDRAWAL);

            // NOTE: Portfolio 업데이트
            updatePortfolio(user, stockCode, orderQuantity, unitPrice, tradeActionType);

        } else if (tradeActionType == TradeActionType.SELL) {
            BigDecimal totalAmount = BigDecimal.valueOf(unitPrice).multiply(BigDecimal.valueOf(orderQuantity));
            // NOTE: Portfolio 업데이트
            updatePortfolio(user, stockCode, orderQuantity, unitPrice, tradeActionType);
            // NOTE: 계좌에 금액 추가 (매도 후 잔액 증가)
            BigDecimal updatedBalance = BigDecimal.valueOf(account.getBalance()).add(totalAmount);
            account.setBalance(updatedBalance.longValue());
            accountService.recordTransaction(account, totalAmount.longValue(), TransactionType.DEPOSIT);
        }

        order.setOrderStatus(OrderStatus.FILLED); // NOTE: 주문 체결 완료 처리, 이때 Dirty checking 적용됨, @Transactional 있다는 가정하에
//        orderRepository.save(order);  // Dirty checking 으로 필요없음
        saveOrderTransaction(order);    // NOTE: 체결완료시 주문체결 저장
    }

    @Override
    public List<OrderResponseDto> getOrders(Long uid, String stockCode, OrderStatus status) {
        // TODO: 추후 필터로직도 추가

        List<Order> orders = orderRepository.findByUserUid(uid);

        return orders.stream().map((order)->{
            String stockName = stockNameCache.getStockNameAndSid(order.getStckCode()).getStckName(); // NOTE: 종목 이름 캐시에서 가져오기
            return OrderResponseDto.builder()
                    .oid(order.getOid())
                    .stckOrdTs(order.getCreatedAt())
                    .stckOrdUnitPrice(order.getStckOrdUnitPrice())
                    .orderType(order.getOrderType())
                    .orderStatus(order.getOrderStatus())
                    .tradeAction(order.getTradeAction())
                    .stckOrdQty(order.getStckOrdQty())
                    .stckName(stockName)
                    .build();
        }).collect(Collectors.toList());
    }

    private void saveOrderTransaction(Order order) {
        OrderTransaction transaction = OrderTransaction.builder()
                .order(order)
                .stckExecUnitPrice(order.getStckOrdUnitPrice()) // NOTE: 체결 가격
                .stckExecQty(order.getStckOrdQty()) // NOTE: 체결 수량
                .build();

        orderTransactionRepository.save(transaction); // NOTE: 새 엔티티는 INSERT 실행 필요
    }


    /**
     *  `Portfolio` 업데이트 (보유 주식 정보 반영), 오직 주문 처리의 일부, 재사용성 아직없음, 추후 필요시 분리
     */
    private void updatePortfolio(User user, String stockCode, Long orderQuantity, Long unitPrice, TradeActionType tradeActionType) {
        Optional<Portfolio> existingPortfolio = portfolioRepository.findByUserAndStckCode(user, stockCode);

        if (tradeActionType == TradeActionType.BUY) {
            if (existingPortfolio.isPresent()) {
                Portfolio portfolio = existingPortfolio.get();
                Long newStockQty = portfolio.getStckQty() + orderQuantity;
                BigDecimal newAvgPrice = calculateNewAveragePrice(portfolio, orderQuantity, unitPrice);
                portfolio.setStckCode(stockCode);
                portfolio.setStckQty(newStockQty);
                portfolio.setAvgPurchasePrice(newAvgPrice);
//                portfolioRepository.save(portfolio); // NOTE: Dirty checking 으로 필요없음
            } else {
                Portfolio newPortfolio = Portfolio.builder()
                        .user(user)
                        .stckCode(stockCode)
                        .stckQty(orderQuantity)
                        .avgPurchasePrice(BigDecimal.valueOf(unitPrice))
                        .build();
                portfolioRepository.save(newPortfolio);  // NOTE: 새 엔티티는 INSERT 실행 필요
            }
        } else if (tradeActionType == TradeActionType.SELL) {
            if (existingPortfolio.isPresent()) {
                Portfolio portfolio = existingPortfolio.get();
                if (portfolio.getStckQty() < orderQuantity) {
                    throw new IllegalArgumentException("Not enough stocks to sell");
                }

                //  보유 주식 수량 감소
                portfolio.setStckQty(portfolio.getStckQty() - orderQuantity);

                //  보유 주식이 0개가 되면 삭제
                if (portfolio.getStckQty() == 0) {
                    portfolioRepository.delete(portfolio);
                }
            } else {
                throw new IllegalArgumentException("No stocks available to sell");
            }
        }
    }

    /**
     *  평균 매입가 계산 메서드
     */
    private BigDecimal calculateNewAveragePrice(Portfolio portfolio, Long newQuantity, Long newPrice) {
        Long currentQty = portfolio.getStckQty();
        BigDecimal currentTotalPrice = portfolio.getAvgPurchasePrice().multiply(BigDecimal.valueOf(currentQty));
        BigDecimal newTotalPrice = BigDecimal.valueOf(newPrice).multiply(BigDecimal.valueOf(newQuantity));
        BigDecimal updatedQty = BigDecimal.valueOf(currentQty).add(BigDecimal.valueOf(newQuantity));
        return (currentTotalPrice.add(newTotalPrice)).divide(updatedQty, 2, RoundingMode.HALF_UP);
    }
}
