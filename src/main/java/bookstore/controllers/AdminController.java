package bookstore.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import bookstore.lib.Statistics;

import java.util.List;
import java.util.Map;

public class AdminController {

    @FXML
    private Label messageLabel;

    @FXML
    private TextField daysTextField;

    @FXML
    private Button enterButton;

    @FXML
    private StackedBarChart<String, Number> salesBarChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    private Statistics statistics;

    @FXML
    private void initialize() {
        messageLabel.setText("Welcome to the Admin Page!");
        statistics = new Statistics();
        xAxis.setLabel("Days Ago");
        yAxis.setLabel("Amount");
    }

    @FXML
    private void handleEnterAction() {
        String daysText = daysTextField.getText();
        int days;
        try {
            days = Integer.parseInt(daysText);
        } catch (NumberFormatException e) {
            messageLabel.setText("Please enter a valid number of days.");
            return;
        }

        if (days > 30) {
            days = 30;
        } else if (days < 1) {
            days = 1;
        }

        // Fetch the daily stats
        List<Map<String, Double>> dailyStats = statistics.getDailyStats(days);

        // Clear previous data
        salesBarChart.getData().clear();

        // Create series for cost and profit
        XYChart.Series<String, Number> costSeries = new XYChart.Series<>();
        costSeries.setName("Cost");

        XYChart.Series<String, Number> profitSeries = new XYChart.Series<>();
        profitSeries.setName("Profit");

        // Populate data
        for (int i = 0; i < days; i++) {
            Map<String, Double> dayStats = dailyStats.get(i);
            String dayLabel = String.valueOf(days - i);

            Double cost = dayStats.get("totalCost");
            Double revenue = dayStats.get("totalSales");
            Double profit = revenue - cost;

            costSeries.getData().add(new XYChart.Data<>(dayLabel, cost));
            profitSeries.getData().add(new XYChart.Data<>(dayLabel, profit));
        }

        // Add series to chart
        salesBarChart.getData().addAll(costSeries, profitSeries);

        // Apply custom colors via CSS
        salesBarChart.lookupAll(".default-color0.chart-bar")
                .forEach(n -> n.setStyle("-fx-bar-fill: yellow;"));
        salesBarChart.lookupAll(".default-color1.chart-bar")
                .forEach(n -> n.setStyle("-fx-bar-fill: blue;"));
    }
}