package stock.mock_stock.integration;


import jakarta.transaction.Transactional;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc  // MockMvc 사용 가능하게 설정
public class WatchlistIntegrationTest {

    // TODO: add

    // TODO: get

    // TODO: delete
}
