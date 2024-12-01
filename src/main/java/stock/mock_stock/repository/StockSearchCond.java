package stock.mock_stock.repository;

import lombok.Data;

@Data
public class StockSearchCond {

    private String searchQuery;

    public StockSearchCond(){

    }

    public StockSearchCond(String searchQuery){
        this.searchQuery = searchQuery;
    }

}
