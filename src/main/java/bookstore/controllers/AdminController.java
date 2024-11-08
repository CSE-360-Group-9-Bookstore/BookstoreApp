package bookstore.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import bookstore.lib.Statistics;

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

    @FXML
    private VBox root;

    private Statistics statistics;

    @FXML
    private void initialize() {
        messageLabel.setText("Welcome to the Admin Page!");
        statistics = new Statistics();

        // Apply hard-coded styles
        root.setStyle("-fx-background-color: #222;");
        messageLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 16px;");
        daysTextField.setStyle("-fx-background-color: #444; -fx-text-fill: #FFFFFF;");
        enterButton.setStyle("-fx-background-color: #444; -fx-text-fill: #FFFFFF; -fx-background-radius: 5;");

        xAxis.setLabel("Date");
        yAxis.setLabel("Amount");

        salesBarChart.setStyle("-fx-background-color: #222;");
        salesBarChart.setLegendVisible(true);
        salesBarChart.setAnimated(false);
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
        for (int i = days - 1; i >= 0; i--) {
            Map<String, Double> dayStats = dailyStats.get(i);

            LocalDate date = LocalDate.now().minusDays(days - 1 - i);
            String dateLabel = date.toString();

            Double cost = dayStats.get("totalCost");
            Double revenue = dayStats.get("totalSales");
            Double profit = revenue - cost;

            // For cost series
            XYChart.Data<String, Number> costData = new XYChart.Data<>(dateLabel, cost);

            // For profit series
            XYChart.Data<String, Number> profitData = new XYChart.Data<>(dateLabel, profit);

            costSeries.getData().add(costData);
            profitSeries.getData().add(profitData);
        }

        // Add series to chart
        salesBarChart.getData().addAll(costSeries, profitSeries);

        // Apply custom colors and tooltips
        for (XYChart.Series<String, Number> series : salesBarChart.getData()) {
            String color;
            if (series.getName().equals("Cost")) {
                color = "#F7DC6F"; // Yellow
            } else {
                color = "#5DADE2"; // Blue
            }

            for (XYChart.Data<String, Number> data : series.getData()) {
                data.getNode().setStyle("-fx-bar-fill: " + color + ";");
                Tooltip tooltip = new Tooltip(
                        series.getName() + "\nDate: " + data.getXValue() + "\nAmount: $" + data.getYValue());
                Tooltip.install(data.getNode(), tooltip);
            }
        }

        // Adjust x-axis labels
        xAxis.setTickLabelRotation(90);
    }
}