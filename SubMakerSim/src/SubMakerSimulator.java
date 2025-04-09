import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class SubMakerSimulator extends JFrame {
    // Variables //
//------------------//
    // Game Time
    private static LocalTime gameTime = LocalTime.of(8, 0); // Start time
    private static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm a");

    // Ingredients List
    private static final String[] loafSize = {"Whole", "Half"};
    private static final String[] breads = {"White", "Italian 5 Grain", "Whole Wheat", "Soft Sub Roll", "Flatbread", "No Bread(Salad)"};
    private static final String[] meats = {"Ultimate", "Italian", "Turkey", "Honey Maple Turkey", "Ham", "Roast Beef", "Chicken Tenders", "Salami"};
    private static final String[] cheeses = {"Provolone", "White American", "Jalapeno Pepper Jack", "Swiss", "Cheddar", "Muenster", "Yellow American", "Chao Vegan", "Smoked Gouda", "No Cheese"};
    private static final String[] toppings = {"Lettuce", "Tomato", "Onions", "Black Pepper", "Salt", "Banana Peppers", "Oil Packets", "Vinegar Packets", 
    "Black Olives", "Spinach", "Oregano", "Green Peppers", "Dill Pickles", "Garlic Pickles(BH)", "Cucumbers", "Jalapenos"};
    private static final String[] sauces = {"Mayo", "Yellow Mustard", "BH Honey Mustard", "BH Spicy Mustard", "BH Chipotle Gourmaise", 
    "BH Sub Dressing", "Deli Sub Sauce", "Buttermilk Ranch", "BH Pepperhouse Gourmaise", "Vegan Ranch", "Lemon Garlic Aiolo", "Vegan Horsey Sauce"};

    // Selected Ingredients
    private String selectedloafSize = "";
    private String selectedBread = "";
    private String selectedMeat = "";
    private String selectedCheese = "";
    private String selectedTopping = "";
    private String selectedSauce = "";

    // Score Counter
    private static int dailyEarnings = 0;
    private static int wrongSubs = 0;
    private static int bankTotal = 0;
    private static String currentOrder = "";

    private Queue<String> orderQueue = new LinkedList<>(); // Queue to hold orders
    private DefaultListModel<String> orderListModel = new DefaultListModel<>(); // Model for the order list

    private int orderCount = 0; // Counter for the number of orders
    private int orderInterval = 20; // Time interval for new orders in seconds
    private int secondsElapsed = 0; // Counter for elapsed time
    private boolean isShiftOver = false; // Flag to check if the shift is over

    // UI Components
    private JLabel timeLabel;
    private JLabel orderLabel;
    private JLabel statusLabel;
    private JLabel earningsLabel;
    private JLabel yourSubLabel;

    private JList<String> orderList; // List to display orders

    private JPanel loafSizePanel;
    private JPanel breadPanel;
    private JPanel meatPanel;
    private JPanel cheesePanel;
    private JPanel toppingPanel;
    private JPanel saucePanel;

    private JButton submitButton;
    private Timer gameTimer;

    // Add Colors
    private final Color PUBLIX_GREEN = new Color(0, 130, 100);
    private final Color PUBLIX_KHAKI = new Color(215, 204, 164);

    // Application //
    public SubMakerSimulator() {
        super("Sub Maker Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 700);
        setLayout(new BorderLayout());

        getContentPane().setBackground(PUBLIX_KHAKI); // Set background color

        initializeUI();
        startGameTimer();

        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initializeUI() {
        // Top panel with time and earnings
        JPanel topPanel = new JPanel(new BorderLayout());
        timeLabel = new JLabel("Current Time: " + gameTime.format(timeFormat), JLabel.CENTER);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timeLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        timeLabel.setForeground(PUBLIX_GREEN); // Set text color

        earningsLabel = new JLabel("Today's Earnings: $0", JLabel.RIGHT);
        earningsLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 20));
        earningsLabel.setForeground(PUBLIX_GREEN);

        topPanel.add(timeLabel, BorderLayout.CENTER);
        topPanel.add(earningsLabel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Order and Status Panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(PUBLIX_KHAKI); // Set background color
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(centerPanel, BorderLayout.CENTER);

        JPanel orderInfoPanel = new JPanel();
        orderInfoPanel.setLayout(new BoxLayout(orderInfoPanel, BoxLayout.Y_AXIS));
        orderInfoPanel.setBackground(PUBLIX_KHAKI); // Set background color

        orderLabel = new JLabel("Waiting for customers...");
        orderLabel.setFont(new Font("Arial", Font.BOLD, 14));
        orderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        orderLabel.setForeground(PUBLIX_GREEN);

        yourSubLabel = new JLabel("Your Sub: ");
        yourSubLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        yourSubLabel.setForeground(PUBLIX_GREEN);

        statusLabel = new JLabel(" ");
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setForeground(PUBLIX_GREEN);

        orderInfoPanel.add(orderLabel);
        orderInfoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        orderInfoPanel.add(yourSubLabel);
        orderInfoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        orderInfoPanel.add(statusLabel);

        JPanel orderListPanel = new JPanel(new BorderLayout());
        orderListPanel.setBackground(PUBLIX_KHAKI); // Set background color
        orderListPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PUBLIX_GREEN), "Order Queue",
            TitledBorder.CENTER, TitledBorder.TOP, null, PUBLIX_GREEN));

        orderList = new JList<>(orderListModel);
        orderList.setBackground(new Color(240, 240, 220)); // Set background color
        orderList.setSelectionBackground(PUBLIX_GREEN); // Set selection color
        orderList.setSelectionForeground(Color.WHITE); // Set selection text color

        JScrollPane listScroller = new JScrollPane(orderList);
        orderListPanel.add(listScroller, BorderLayout.CENTER);

        centerPanel.add(orderInfoPanel, BorderLayout.NORTH);
        centerPanel.add(orderListPanel, BorderLayout.CENTER);


        // Ingredient Selection Panel
        JPanel ingredientsPanel = new JPanel();
        ingredientsPanel.setLayout(new BoxLayout(ingredientsPanel, BoxLayout.Y_AXIS));
        ingredientsPanel.setBackground(PUBLIX_KHAKI); // Set background color

        loafSizePanel = createIngredientPanel("Whole or Half?", loafSize);
        breadPanel = createIngredientPanel("Bread?", breads);
        meatPanel = createIngredientPanel("Meat?", meats);
        cheesePanel = createIngredientPanel("Cheese?", cheeses);
        toppingPanel = createIngredientPanel("Topping?", toppings);
        saucePanel = createIngredientPanel("Sauce?", sauces);

        ingredientsPanel.add(loafSizePanel);
        ingredientsPanel.add(breadPanel);
        ingredientsPanel.add(meatPanel);
        ingredientsPanel.add(cheesePanel);
        ingredientsPanel.add(toppingPanel);
        ingredientsPanel.add(saucePanel);

        // Submit Button
        JPanel submitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        submitPanel.setBackground(PUBLIX_KHAKI); // Set background color

        submitButton = new JButton("Submit Order");
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitButton.setEnabled(false); // Initially disabled
        submitButton.setBackground(PUBLIX_GREEN); // Set button color
        submitButton.setForeground(Color.BLACK); // Set button text color
        setFocusable(false); // Prevent focus on the button
        submitButton.addActionListener(e -> checkOrder());

        submitPanel.add(submitButton);
        ingredientsPanel.add(submitPanel);

        JScrollPane scrollPane = new JScrollPane(ingredientsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Scroll speed

        add(scrollPane, BorderLayout.SOUTH);
    }

    private JPanel createIngredientPanel(String title, String[] options) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PUBLIX_GREEN), title,
            TitledBorder.LEFT, TitledBorder.TOP, null, PUBLIX_GREEN));
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(PUBLIX_KHAKI); // Set background color

        ButtonGroup group = new ButtonGroup();

        for (String option : options) {
            JRadioButton button = new JRadioButton(option);
            button.setBackground(PUBLIX_KHAKI); // Set button background color
            button.setForeground(PUBLIX_GREEN); // Set button text color
            button.addActionListener(e -> {
                switch (title) {
                    case "Whole or Half?":
                        selectedloafSize = option;
                        break;
                    case "Bread?":
                        selectedBread = option;
                        break;
                    case "Meat?":
                        selectedMeat = option;
                        break;
                    case "Cheese?":
                        selectedCheese = option;
                        break;
                    case "Topping?":
                        selectedTopping = option;
                        break;
                    case "Sauce?":
                        selectedSauce = option;
                        break;
                }
                updateYourSubLabel();
                checkIfAllSelected();
            });
            group.add(button);
            panel.add(button);
        }

        return panel;

    }

    private void updateYourSubLabel() {
        StringBuilder sb = new StringBuilder("Your Sub: ");
        if (!selectedloafSize.isEmpty()) sb.append(selectedloafSize);
        if (!selectedBread.isEmpty()) sb.append(selectedBread);
        if (!selectedMeat.isEmpty()) sb.append(", ").append(selectedMeat);
        if (!selectedCheese.isEmpty()) sb.append(", ").append(selectedCheese);
        if (!selectedTopping.isEmpty()) sb.append(", ").append(selectedTopping);
        if (!selectedSauce.isEmpty()) sb.append(", ").append(selectedSauce);

        yourSubLabel.setText(sb.toString());
    }

    private void checkIfAllSelected() {
        boolean allSelected = !selectedloafSize.isEmpty() && !selectedBread.isEmpty() && !selectedMeat.isEmpty() &&
                !selectedCheese.isEmpty() && !selectedTopping.isEmpty() && !selectedSauce.isEmpty();

        submitButton.setEnabled(allSelected);
    }

    private void startGameTimer() {
        gameTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                secondsElapsed++;
                gameTime = gameTime.plusMinutes(1); // Each second = 1 minutes in game time
                timeLabel.setText("Current Time: " + gameTime.format(timeFormat));

                // Check if it's time to generate a new order
                LocalTime currentTime = gameTime;
                boolean isLunchRush = currentTime.isAfter(LocalTime.of(11, 0)) &&
                                      currentTime.isBefore(LocalTime.of(15, 0)); // Lunch rush time

                // During lunch Rush generate orders every 10 seconds
                orderInterval = isLunchRush ? 10 : 20; // 10 seconds during lunch rush, 20 seconds otherwise

                if (secondsElapsed % orderInterval == 0 && !isShiftOver) {
                    generateOrder();
                }
                
                // Check if shift has ended (Either time or finsished all orders)
                if (gameTime.equals(LocalTime.of(17, 0))) {
                    isShiftOver = true; // Set flag to true when shift ends

                    if (orderQueue.isEmpty()) {
                        gameTimer.stop(); // Stop the timer
                        JOptionPane.showMessageDialog(null, "Shift over! Time to clock out.");
                        endOfDayReport();
                    }
                    statusLabel.setText("Store closed! Complete remaining orders.");

                }
            }
        });

        gameTimer.start();
    }

    private void generateOrder() {
        Random rand = new Random();

        String loaf = loafSize[rand.nextInt(loafSize.length)];
        String bread = breads[rand.nextInt(breads.length)];
        String meat = meats[rand.nextInt(meats.length)];
        String cheese = cheeses[rand.nextInt(cheeses.length)];
        String topping = toppings[rand.nextInt(toppings.length)];
        String sauce = sauces[rand.nextInt(sauces.length)];

        String newOrder = loaf + ", " + bread + ", " + meat + ", " + cheese + ", " + topping + ", " + sauce;


        // Add to order queue
        orderQueue.add(newOrder);
        orderCount++;
        orderListModel.addElement("Order " + orderCount + ": " + newOrder); // Add to the list model

        // If this is the first order, set it as current order
        if (currentOrder.isEmpty() || orderLabel.getText().equals("Waiting for customers..."))  {
            currentOrder = newOrder;
            orderLabel.setText("Customer Order: " + currentOrder);
            statusLabel.setText("New order received! Build the sub.");
        } else {
            statusLabel.setText("New order added to the queue!");
        }

    }

    private void resetSelections() {
        selectedloafSize = ""; // Reset selected ingredients
        selectedBread = "";
        selectedMeat = "";
        selectedCheese = "";
        selectedTopping = "";
        selectedSauce = "";

        resetButtonGroup(loafSizePanel);
        resetButtonGroup(breadPanel);
        resetButtonGroup(meatPanel);
        resetButtonGroup(cheesePanel);
        resetButtonGroup(toppingPanel);
        resetButtonGroup(saucePanel);

        updateYourSubLabel();
        submitButton.setEnabled(false); // Disable the button until all ingredients are selected
    }

    private void resetButtonGroup(JPanel panel) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JRadioButton) {
                JRadioButton radioButton = (JRadioButton) comp;

                ActionListener[] listeners = radioButton.getActionListeners();
                for (ActionListener listener : listeners) {
                    radioButton.removeActionListener(listener); // Remove action listeners
                }

                radioButton.setSelected(false); // Deselect the button

                for (ActionListener listener : listeners) {
                    radioButton.addActionListener(listener); // Re-add action listeners
                }
            }
        }
    }

    private void checkOrder() {
        String playerSub = selectedloafSize + ", " + selectedBread + ", " + selectedMeat + ", " 
        + selectedCheese + ", " + selectedTopping + ", " + selectedSauce;

        if (playerSub.equalsIgnoreCase(currentOrder)) {
            statusLabel.setText("Correct! You made $10!");
            statusLabel.setForeground(Color.BLACK);
            dailyEarnings += 10; // Add $10 for each correct order
            earningsLabel.setText("Today's Earnings: $" + dailyEarnings); // Update earnings label

        // Remove completed order from the List
            if (!orderListModel.isEmpty()) {
                orderListModel.remove(0);
            }

            // Get next order from the queue if available
            orderQueue.poll();

            resetSelections(); // Reset selections for the next order

            if (!orderQueue.isEmpty()) {
                currentOrder = orderQueue.peek(); // Get the next order
                orderLabel.setText("Customer Order: " + currentOrder);
            } else {
                currentOrder = ""; // No more orders in the queue
                orderLabel.setText("Waiting for customers...");
            }

        // Is shift over and all orders are completed
            if (isShiftOver && orderQueue.isEmpty()) {
                gameTimer.stop();
                JOptionPane.showMessageDialog(null, "All orders completed! Time to clock out.");
                endOfDayReport(); // Show end of day report
            }
        } else {
            statusLabel.setText("Incorrect! No Money earned.");
            statusLabel.setForeground(Color.RED);
            wrongSubs++;
        }
    }

    private void endOfDayReport() {
        bankTotal += dailyEarnings; // Add daily earnings to bank total
        
        JPanel reportPanel = new JPanel();
        reportPanel.setLayout(new BoxLayout(reportPanel, BoxLayout.Y_AXIS));
        reportPanel.setBackground(PUBLIX_KHAKI); // Set background color

        JLabel titleLabel = new JLabel("End of Shift Report");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(PUBLIX_GREEN); // Set text color

        JLabel profitLabel = new JLabel("Today's Profit: $" + dailyEarnings);
        profitLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        profitLabel.setForeground(Color.BLACK); // Set text color

        JLabel wrongSubsLabel = new JLabel("Total Wrong Subs: " + wrongSubs);
        wrongSubsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        wrongSubsLabel.setForeground(Color.BLACK); // Set text color

        JLabel bankTotalLabel = new JLabel("Bank Total: $" + bankTotal);
        bankTotalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        bankTotalLabel.setForeground(Color.BLACK); // Set text color

        reportPanel.add(titleLabel);
        reportPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        reportPanel.add(profitLabel);
        reportPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        reportPanel.add(wrongSubsLabel);
        reportPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        reportPanel.add(bankTotalLabel);

        JOptionPane.showMessageDialog(null, reportPanel, "End of Day Report", JOptionPane.INFORMATION_MESSAGE);

        int choice = JOptionPane.showConfirmDialog(null, "Work another shift?", 
        "Play Again", 
        JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            gameTime = LocalTime.of(8, 0); // Reset game time to 8:00 AM
            dailyEarnings = 0; // Reset daily earnings
            wrongSubs = 0; // Reset wrong subs count
            orderCount = 0; // Reset order count
            earningsLabel.setText("Today's Earnings: $0"); // Reset earnings label
            orderLabel.setText("Waiting for customers..."); // Reset order label
            statusLabel.setText(" "); // Reset status label

            orderQueue.clear(); // Clear the order queue
            orderListModel.clear(); // Clear the order list model

            currentOrder = ""; // Reset current order
            isShiftOver = false; // Reset shift over flag
            secondsElapsed = 0; // Reset elapsed time

            resetSelections(); // Reset selections for the new shift
            gameTimer.start(); // Restart the game timer
        } else {
            System.exit(0); // Exit the application
        }
    }

public static void main(String[] args) {
    System.out.println("Starting Sub Maker Simulator...");
    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
        e.printStackTrace();
    }

    SwingUtilities.invokeLater(() -> {
        System.out.println("Creating GUI...");
    new SubMakerSimulator();
});
}
}
