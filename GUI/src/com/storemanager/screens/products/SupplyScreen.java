package com.storemanager.screens.products;

import com.storemanager.components.AbstractPanel;
import com.storemanager.components.ImageButton;
import com.storemanager.components.Window;
import com.storemanager.service.ServiceLocator;
import com.storemanager.service.ServiceName;
import com.storemanager.service.SupplyService;
import com.storemanager.util.*;
import org.joda.time.Chronology;
import org.joda.time.DateTimeField;
import org.joda.time.LocalDate;
import org.joda.time.chrono.ISOChronology;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

// SUPPLY SCREEN
public class SupplyScreen extends AbstractPanel {
    private Window baseWindow;
    private JButton close, generate;
    private JRadioButton daily, monthly, period;
    private JLabel yFromL, mFromL, dFromL, yToL, mToL, dToL;
    private JComboBox yFrom, mFrom, dFrom, yTo, mTo, dTo;
    private JPanel searchPanel;

    public SupplyScreen(Window baseWindow) {
        this.baseWindow = baseWindow;

        this.setSize(800, 600);
        this.setBackground(Color.gray);
        this.setLayout(null);

        close = new ImageButton(ButtonEnum.CLOSE, this);
        close.setLocation(670, 530);
        close.setSize(ButtonSizeEnum.DEFAULT.getSize());
        this.add(close);

        generate = new ImageButton(ButtonEnum.GENERATE, this);
        generate.setLocation(300, 520);
        generate.setText("Supply Report");
        generate.setSize(ButtonSizeEnum.LARGE_LONG.getSize());
        this.add(generate);

        createSearchCriteria();

        this.repaint();
        this.validate();
    }

    private void createSearchCriteria() {
        searchPanel = new JPanel();
        searchPanel.setBounds(30, 30, 740, 190);
        searchPanel.setBackground(Color.lightGray);
        searchPanel.setLayout(null);
        this.add(searchPanel);

        JLabel criteriaType = new JLabel("Supply Report");
        criteriaType.setBounds(25, 10, 120, 30);
        searchPanel.add(criteriaType);

        SearchCriteriaListener listener = new SearchCriteriaListener();

        ButtonGroup group = new ButtonGroup();
        daily = new JRadioButton("Daily report", false);
        daily.setBounds(25, 40, 140, 40);
        daily.addActionListener(listener);
        group.add(daily);
        searchPanel.add(daily);

        monthly = new JRadioButton("Monthly report", false);
        monthly.setBounds(25, 80, 140, 40);
        monthly.addActionListener(listener);
        group.add(monthly);
        searchPanel.add(monthly);

        period = new JRadioButton("Time period report", false);
        period.setBounds(25, 120, 140, 40);
        period.addActionListener(listener);
        group.add(period);
        searchPanel.add(period);

        //FROM
        yFromL = new JLabel("Year:");
        yFromL.setBounds(200, 40, 50, 30);
        searchPanel.add(yFromL);
        yFrom = new JComboBox(getYearList());
        yFrom.setBounds(240, 40, 80, 30);
        yFrom.addActionListener(this);
        searchPanel.add(yFrom);

        mFromL = new JLabel("Month:");
        mFromL.setBounds(350, 40, 50, 30);
        searchPanel.add(mFromL);
        mFrom = new JComboBox(getMonthList());
        mFrom.setBounds(400, 40, 120, 30);
        mFrom.addActionListener(this);
        searchPanel.add(mFrom);

        dFromL = new JLabel("Day:");
        dFromL.setBounds(550, 40, 50, 30);
        searchPanel.add(dFromL);
        dFrom = new JComboBox(getDayList(Integer.parseInt(yFrom.getSelectedItem().toString()), mFrom.getSelectedIndex()));
        dFrom.setBounds(590, 40, 80, 30);
        searchPanel.add(dFrom);

        //TO
        yToL = new JLabel("Year:");
        yToL.setBounds(200, 90, 50, 30);
        searchPanel.add(yToL);
        yTo = new JComboBox(getYearList());
        yTo.setBounds(240, 90, 80, 30);
        yTo.addActionListener(this);
        searchPanel.add(yTo);

        mToL = new JLabel("Month:");
        mToL.setBounds(350, 90, 50, 30);
        searchPanel.add(mToL);
        mTo = new JComboBox(getMonthList());
        mTo.setBounds(400, 90, 120, 30);
        mTo.addActionListener(this);
        searchPanel.add(mTo);

        dToL = new JLabel("Day:");
        dToL.setBounds(550, 90, 50, 30);
        searchPanel.add(dToL);
        dTo = new JComboBox(getDayList(Integer.parseInt(yFrom.getSelectedItem().toString()), mFrom.getSelectedIndex()));
        dTo.setBounds(590, 90, 80, 30);
        searchPanel.add(dTo);

        hideAllCriteria();
        this.repaint();
        this.validate();
    }

    private void hideAllCriteria() {
        if (yFromL != null)
            yFromL.setVisible(false);
        if (mFromL != null)
            mFromL.setVisible(false);
        if (dFromL != null)
            dFromL.setVisible(false);
        if (yToL != null)
            yToL.setVisible(false);
        if (mToL != null)
            mToL.setVisible(false);
        if (dToL != null)
            dToL.setVisible(false);
        if (yFrom != null)
            yFrom.setVisible(false);
        if (mFrom != null)
            mFrom.setVisible(false);
        if (dFrom != null)
            dFrom.setVisible(false);
        if (yTo != null)
            yTo.setVisible(false);
        if (mTo != null)
            mTo.setVisible(false);
        if (dTo != null)
            dTo.setVisible(false);
    }

    private void showFromSearchCriteria() {
        if (yFromL != null)
            yFromL.setVisible(true);
        if (mFromL != null)
            mFromL.setVisible(true);
        if (dFromL != null)
            dFromL.setVisible(true);
        if (yFrom != null)
            yFrom.setVisible(true);
        if (mFrom != null)
            mFrom.setVisible(true);
        if (dFrom != null)
            dFrom.setVisible(true);
        this.repaint();
        this.validate();
    }

    private void showToSearchCriteria() {
        if (yToL != null)
            yToL.setVisible(true);
        if (mToL != null)
            mToL.setVisible(true);
        if (dToL != null)
            dToL.setVisible(true);
        if (yTo != null)
            yTo.setVisible(true);
        if (mTo != null)
            mTo.setVisible(true);
        if (dTo != null)
            dTo.setVisible(true);
        this.repaint();
        this.validate();
    }

    private class SearchCriteriaListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(daily)) {
                hideAllCriteria();
                showFromSearchCriteria();
            } else if (e.getSource().equals(monthly)) {
                hideAllCriteria();
                showFromSearchCriteria();
                dFrom.setVisible(false);
                dFromL.setVisible(false);
            } else if (e.getSource().equals(period)) {
                hideAllCriteria();
                showFromSearchCriteria();
                showToSearchCriteria();
            }
        }
    }

    private Object[] getYearList() {
        return getYearList(-1);
    }

    private Object[] getYearList(int year) {
        java.util.List<String> list = new ArrayList<String>();
        int startYear = 2013;
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = currentYear; i >= startYear; i--) {
            list.add(Integer.toString(i));
        }
        return list.toArray();
    }

    private Object[] getMonthList() {
        return getMonthList(-1);
    }

    private Object[] getMonthList(int index) {
        java.util.List<String> list = new ArrayList<String>();
        int startMonth = 0;
        if (index > startMonth) {
            startMonth = index;
        }
        String[] months = new DateFormatSymbols().getMonths();
        for (int i = startMonth; i < months.length; i++) {
            String month = months[i];
            if (!Strings.isEmpty(month)) {
                list.add(month);
            }
        }
        return list.toArray();
    }

    private Object[] getDayList(int year, int month) {
        return getDayList(year, month, -1);
    }

    private Object[] getDayList(int year, int month, int day) {
        java.util.List<String> list = new ArrayList<String>();
        int startDay = 1;
        if (day > startDay) {
            startDay = day;
        }
        int maxDays = getDaysInMonth(year, month + 1);
        for (int i = startDay; i <= maxDays; i++) {
            list.add(Integer.toString(i));
        }
        return list.toArray();
    }

    public int getDaysInMonth(int year, int month) {
        Chronology chrono = ISOChronology.getInstance();
        DateTimeField dayField = chrono.dayOfMonth();
        LocalDate monthDate = new LocalDate(year, month, 1);
        return dayField.getMaximumValue(monthDate);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(mFrom) || e.getSource().equals(yFrom)) {
            dFrom.removeAllItems();
            Object[] days = getDayList(Integer.parseInt(yFrom.getSelectedItem().toString()), mFrom.getSelectedIndex());
            for (Object item : days) {
                dFrom.addItem(item);
            }
            return;
        }
        if (e.getSource().equals(mTo) || e.getSource().equals(yTo)) {
            dTo.removeAllItems();
            Object[] days = getDayList(Integer.parseInt(yTo.getSelectedItem().toString()), mTo.getSelectedIndex());
            for (Object item : days) {
                dTo.addItem(item);
            }
            return;
        }

        ImageButton trigger = (ImageButton) e.getSource();

        if (trigger.getCommand().equals(ButtonEnum.CLOSE.getCommand())) {
            baseWindow.closeScreen();
        }
        if (trigger.getCommand().equals(ButtonEnum.GENERATE.getCommand())) {
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            Date startDate = null, endDate = null;
            try {
                if (monthly.isSelected()) {
                    startDate = df.parse((mFrom.getSelectedIndex() + 1) + "/1/" + yFrom.getSelectedItem().toString());
                    int lastDay = getDaysInMonth(Integer.parseInt(yFrom.getSelectedItem().toString()), (mFrom.getSelectedIndex() + 1));
                    endDate = df.parse((mFrom.getSelectedIndex() + 1) + "/" + lastDay + "/" + yTo.getSelectedItem().toString());
                } else {
                    startDate = df.parse((mFrom.getSelectedIndex() + 1) + "/" + dFrom.getSelectedItem().toString() + "/" + yFrom.getSelectedItem().toString());
                    if (period.isSelected()) {
                        endDate = df.parse((mTo.getSelectedIndex() + 1) + "/" + dTo.getSelectedItem().toString() + "/" + yTo.getSelectedItem().toString());
                    }
                }
            } catch (ParseException pse) {
                Message.showError("Error creating report dates: " + pse.getMessage());
            }
            SupplyService supplyService = ServiceLocator.getService(ServiceName.SUPPLY_SERVICE);
            try {
                supplyService.generateSupplyReport(startDate, endDate);
            } catch (ServiceException e1) {
                Message.show(e1);
            }
        }
    }
}
