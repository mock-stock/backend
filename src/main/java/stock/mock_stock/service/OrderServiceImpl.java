package stock.mock_stock.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stock.mock_stock.entity.*;
import stock.mock_stock.repository.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final PortfolioRepository portfolioRepository;
    private final AccountRepository accountRepository;
    private final OrderTransactionRepository orderTransactionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void processOrder(Long userId, String stockCode, Long quantity, Long price, OrderType orderType, TradeActionType tradeActionType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // NOTE: 주문 기록
        Order order = Order.builder()
                .user(user)
                .stockCode(stockCode)
                .stckOrdQty(quantity)
                .stckOrdUnitPrice(price)
                .orderType(orderType)
                .orderStatus(OrderStatus.PENDING) // 주문 초기 상태
                .build();
        orderRepository.save(order); // 새 엔티티는 INSERT 실행 필요

        Account account = accountRepository.findByUserUid(userId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        // NOTE: 계좌 잔액 차감 (매수 시)
        if (tradeActionType == TradeActionType.BUY) {
            BigDecimal totalCost = BigDecimal.valueOf(price).multiply(BigDecimal.valueOf(quantity));

            if (BigDecimal.valueOf(account.getBalance()).compareTo(totalCost) < 0) {
                throw new IllegalArgumentException("잔액 부족");
            }
            BigDecimal updatedBalance = BigDecimal.valueOf(account.getBalance()).subtract(totalCost);
            account.setBalance(updatedBalance.longValue()); // 이때 Dirty checking 적용됨, @Transactional 있다는 가정하에
//            accountRepository.save(account); // Dirty checking 으로 필요없음

            // NOTE: Portfolio 업데이트
            updatePortfolio(user, stockCode, quantity, price, tradeActionType);

        }  else if (tradeActionType == TradeActionType.SELL) {
            BigDecimal totalRevenue = BigDecimal.valueOf(price).multiply(BigDecimal.valueOf(quantity));
            // NOTE: Portfolio 업데이트
            updatePortfolio(user, stockCode, quantity, price, tradeActionType);
            // NOTE: 계좌에 금액 추가 (매도 후 잔액 증가)
            BigDecimal updatedBalance = BigDecimal.valueOf(account.getBalance()).add(totalRevenue);
            account.setBalance(updatedBalance.longValue());
        }



        // NOTE: 주문 체결 완료 처리
        order.setOrderStatus(OrderStatus.FILLED); // 이때 Dirty checking 적용됨, @Transactional 있다는 가정하에
//        orderRepository.save(order);  // Dirty checking 으로 필요없음

        // NOTE: `OrderTransaction` 저장
        saveOrderTransaction(order);
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
     *  `Portfolio` 업데이트 (보유 주식 정보 반영)
     */
    private void updatePortfolio(User user, String stockCode, Long quantity, Long price, TradeActionType tradeActionType) {
        Optional<Portfolio> existingPortfolio = portfolioRepository.findByUserAndStckCode(user, stockCode);

        if (tradeActionType == TradeActionType.BUY) {
            if (existingPortfolio.isPresent()) {
                Portfolio portfolio = existingPortfolio.get();
                Long newStockQty = portfolio.getStckQty() + quantity;
                BigDecimal newAvgPrice = calculateNewAveragePrice(portfolio, quantity, price);
                portfolio.setStckCode(stockCode);
                portfolio.setStckQty(newStockQty);
                portfolio.setAvgPurchasePrice(newAvgPrice);
//                portfolioRepository.save(portfolio); // NOTE: Dirty checking 으로 필요없음
            } else {

                Portfolio newPortfolio = Portfolio.builder()
                        .user(user)
                        .stckCode(stockCode)
                        .stckQty(quantity)
                        .avgPurchasePrice(BigDecimal.valueOf(price))
                        .build();
                portfolioRepository.save(newPortfolio);  // NOTE: 새 엔티티는 INSERT 실행 필요
            }
        } else if (tradeActionType == TradeActionType.SELL) {
            if (existingPortfolio.isPresent()) {
                Portfolio portfolio = existingPortfolio.get();
                if (portfolio.getStckQty() < quantity) {
                    throw new IllegalArgumentException("Not enough stocks to sell");
                }

                //  보유 주식 수량 감소
                portfolio.setStckQty(portfolio.getStckQty() - quantity);

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
