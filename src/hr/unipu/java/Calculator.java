package hr.unipu.java;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Calculator extends JFrame {
    public static void main(String[] args){
        Calculator calculator = new Calculator();
        calculator.setTitle("Simple Calculator");
        calculator.pack();
        calculator.setResizable(false);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        calculator.setLocation(dim.width/2-calculator.getSize().width/2, dim.height/2-calculator.getSize().height/2);
        calculator.setVisible(true);
    }

    private JTextField firstNum, secondNum, solution;

    public Calculator(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        GridLayout gridLayout = new GridLayout(4,1);
        panel.setLayout(gridLayout);
        setContentPane(panel);

        JPanel feelPanel = new JPanel();
        FlowLayout feelLayout = new FlowLayout();
        feelPanel.setLayout(feelLayout);
        UIManager.LookAndFeelInfo[] feelStyles = UIManager.getInstalledLookAndFeels();
        for (UIManager.LookAndFeelInfo feelInfo : feelStyles) {
            JButton lfButton = new JButton(feelInfo.getName());
            feelPanel.add(lfButton);
            lfButton.addActionListener((actionEvent) -> {
                try {
                    UIManager.setLookAndFeel(feelInfo.getClassName());
                    SwingUtilities.updateComponentTreeUI(Calculator.this);
                } catch (Exception e) { System.err.println("Error loading LookandFeel."); }
            });
        }
        panel.add(feelPanel);

        JPanel textFieldPanel = new JPanel();
        FlowLayout textFieldLayout = new FlowLayout();
        textFieldPanel.setLayout(textFieldLayout);
        firstNum = new JTextField();
        firstNum.setPreferredSize(new Dimension(150,50));
        firstNum.setHorizontalAlignment(SwingConstants.CENTER);
        secondNum = new JTextField();
        secondNum.setPreferredSize(new Dimension(150,50));
        secondNum.setHorizontalAlignment(SwingConstants.CENTER);
        textFieldPanel.add(firstNum);
        textFieldPanel.add(secondNum);
        panel.add(textFieldPanel);

        JPanel buttonPanel = new JPanel();
        FlowLayout buttonLayout = new FlowLayout();
        buttonPanel.setLayout(buttonLayout);
        JButton plus = new JButton("+");
        JButton minus = new JButton("-");
        JButton times = new JButton("x");
        JButton divide = new JButton("/");
        plus.setPreferredSize(new Dimension(50,50));
        minus.setPreferredSize(new Dimension(50,50));
        times.setPreferredSize(new Dimension(50,50));
        divide.setPreferredSize(new Dimension(50,50));
        buttonPanel.add(plus);
        buttonPanel.add(minus);
        buttonPanel.add(times);
        buttonPanel.add(divide);
        panel.add(buttonPanel);

        solution = new JTextField("Solution");
        solution.setEditable(false);
        solution.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(solution);

        plus.addActionListener(this::calculate);
        minus.addActionListener(this::calculate);
        times.addActionListener(this::calculate);
        divide.addActionListener(this::calculate);
    }

    public void calculate(ActionEvent event){
        new Thread(() -> {
            String firstNumStr = firstNum.getText();
            String secondNumStr = secondNum.getText();
            if (!isNum(firstNumStr) && !isNum(secondNumStr)) solution.setText("Both number inputs incorrect.");
            else if (!isNum(firstNumStr)) solution.setText("First number input incorrect.");
            else if (!isNum(secondNumStr)) solution.setText("Second number input incorrect.");
            else {
                String operation = event.getActionCommand();
                double firstNumUse = Double.parseDouble(firstNumStr);
                double secondNumUse = Double.parseDouble(secondNumStr);
                new CalculateTask(firstNumUse, secondNumUse, operation).execute();
            }
        }).start();
    }

    public static boolean isNum(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    private class CalculateTask extends SwingWorker<Double, Double> {
        private double firstNum;
        private double secondNum;
        private String operation;

        CalculateTask(double firstNum, double secondNum, String operation){
            this.firstNum = firstNum;
            this.secondNum = secondNum;
            this.operation = operation;
        }

        @Override
        protected Double doInBackground() throws Exception {
            double sol = 0;
            if(operation == "+") { sol = firstNum + secondNum; }
            else if(operation == "-") { sol = firstNum - secondNum; }
            else if(operation == "x") { sol = firstNum * secondNum; }
            else if(operation == "/"){
                if(this.secondNum == 0.0){
                    solution.setText("Division by 0 error.");
                    throw new ArithmeticException("Division by 0 error.");
                } else { sol = firstNum / secondNum; }
            }
            return sol;
        }

        @Override
        protected void done() {
            try { solution.setText(String.valueOf(this.get())); }
            catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) { e.printStackTrace(); }
        }
    }
}
