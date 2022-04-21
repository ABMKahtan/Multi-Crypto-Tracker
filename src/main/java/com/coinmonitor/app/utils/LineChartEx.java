package com.coinmonitor.app.utils;

import com.coinmonitor.app.model.CryptoPrice;
import lombok.NoArgsConstructor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;


@NoArgsConstructor
public class LineChartEx extends JFrame {

    public JFreeChart generateChart(String coinId, Map<String, CryptoPrice> priceMap) {

        TimeSeriesCollection dataset = createDataset(priceMap);
        JFreeChart chart = createChart(coinId, dataset);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel.setBackground(Color.white);
        setSize(1500, 1000);

        return chart;

    }

    private TimeSeriesCollection createDataset(Map<String, CryptoPrice> priceMap) {
        TimeSeries series = new TimeSeries("Last 24 hours");

        for (CryptoPrice cryptoPrice : priceMap.values()) {
            LocalDateTime ldt = LocalDateTime.ofInstant(cryptoPrice.getDate(), ZoneId.systemDefault());
            Minute minute = new Minute(ldt.getMinute(), ldt.getHour(), ldt.getDayOfMonth(), ldt.getMonthValue(), ldt.getYear());
            series.add(minute, cryptoPrice.getPrice());
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(series);

        return dataset;
    }

    private JFreeChart createChart(String coinId, TimeSeriesCollection dataset) {

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Last 24 hours price for " + coinId,
                "Time",
                "Price (USD)",
                dataset,
                true,
                false,
                false
        );

        XYPlot plot = chart.getXYPlot();

        var renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);

        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);

        chart.getLegend().setFrame(BlockBorder.NONE);

        DateAxis domain = (DateAxis) plot.getDomainAxis();
        domain.setDateFormatOverride(new SimpleDateFormat("YYYY-MM-dd HH:mm"));

        chart.setTitle(new TextTitle("Price in the last 24 hours for " + coinId.toUpperCase(),
                        new Font("Serif", java.awt.Font.BOLD, 18)
                )
        );

        return chart;
    }

}
