package control;

import boundary.StockMaintenanceUI;
import control.StockMaintenance;

public class TestStockMaintenanceUI {
    public static void main(String[] args) {
        StockMaintenance stock = new StockMaintenance();
        StockMaintenanceUI ui = new StockMaintenanceUI(stock);
        ui.run();
    }
}