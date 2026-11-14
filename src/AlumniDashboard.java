import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AlumniDashboard extends JFrame {

    public AlumniDashboard() {
        setTitle("Alumni Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(25, 42, 86));
        headerPanel.setPreferredSize(new Dimension(1100, 70));
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel("Alumni Portal");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        profilePanel.setOpaque(false);

        JLabel profileIcon = new JLabel("👤 Profile");
        profileIcon.setFont(new Font("SansSerif", Font.BOLD, 14));
        profileIcon.setForeground(Color.WHITE);

        JComboBox<String> profileDropdown = new JComboBox<>(new String[]{"Account Settings", "Add Detail", "Edit Detail", "Logout"});
        profileDropdown.setFocusable(false);
        profileDropdown.setBackground(Color.WHITE);

        profileDropdown.addActionListener(e -> {
            String selected = (String) profileDropdown.getSelectedItem();
            if ("Add Detail".equals(selected)) {
                openAddDetailWindow();
            } else if ("Edit Detail".equals(selected)) {
                openEditProfileWindow();
            } else if ("Logout".equals(selected)) {
                dispose();
                LoginPage.main(new String[]{});
            }
        });

        profilePanel.add(profileIcon);
        profilePanel.add(profileDropdown);
        headerPanel.add(profilePanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        JPanel mainBody = new JPanel(new GridBagLayout());
        mainBody.setBackground(new Color(245, 246, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel imageLabel = createImageLabel("src/pietlogo.png", 900, 140);
        gbc.gridy = 0;
        mainBody.add(imageLabel, gbc);

        JPanel cardsGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        cardsGrid.setOpaque(false);

        Color skyBlue = new Color(224, 247, 250);
        Color skyBlueHover = new Color(178, 235, 242);

        JPanel alumniCard = createHoverCard("Registered Alumni", "View Directory", skyBlue, skyBlueHover);
        JPanel reunionCard = createHoverCard("Upcoming Reunion", "Grand Meet 2026", skyBlue, skyBlueHover);
        JPanel pastCard = createHoverCard("Past Reunions", "View Archives & Photos", skyBlue, skyBlueHover);
        JPanel participantCard = createHoverCard("Participant Name", "RSVP Directory", skyBlue, skyBlueHover);

        alumniCard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openRegisteredAlumniWindow();
            }
        });

        cardsGrid.add(alumniCard);
        cardsGrid.add(reunionCard);
        cardsGrid.add(pastCard);
        cardsGrid.add(participantCard);

        gbc.gridy = 1;
        mainBody.add(cardsGrid, gbc);

        add(mainBody, BorderLayout.CENTER);
    }

    private void openAddDetailWindow() {
        JDialog dialog = new JDialog(this, "Add Alumni Detail", true);
        dialog.setSize(450, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 15));
        formPanel.setBorder(new EmptyBorder(25, 30, 15, 30));
        formPanel.setBackground(Color.WHITE);

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField mobileField = new JTextField();
        JTextField companyField = new JTextField();
        JTextField designationField = new JTextField();
        JTextField ctcField = new JTextField();
        JTextField experienceField = new JTextField();

        formPanel.add(new JLabel("Full Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Email Address:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Mobile No:"));
        formPanel.add(mobileField);
        formPanel.add(new JLabel("Company Name:"));
        formPanel.add(companyField);
        formPanel.add(new JLabel("Designation:"));
        formPanel.add(designationField);
        formPanel.add(new JLabel("Package (CTC):"));
        formPanel.add(ctcField);
        formPanel.add(new JLabel("Experience:"));
        formPanel.add(experienceField);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        bottomPanel.setBackground(Color.WHITE);

        JButton submitBtn = new JButton("Submit");
        submitBtn.setPreferredSize(new Dimension(140, 38));
        submitBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        submitBtn.setBackground(new Color(41, 128, 185));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFocusPainted(false);
        submitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        submitBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String mobile = mobileField.getText().trim();
            String company = companyField.getText().trim();
            String designation = designationField.getText().trim();
            String ctc = ctcField.getText().trim();
            String experience = experienceField.getText().trim();

            if (name.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Name and Email are required fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String sql = "UPDATE student SET name = ?, mobile = ?, company = ?, designation = ?, ctc = ?, experience = ? WHERE email = ?";
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setString(2, mobile);
                ps.setString(3, company);
                ps.setString(4, designation);
                ps.setString(5, ctc);
                ps.setString(6, experience);
                ps.setString(7, email);
                int rows = ps.executeUpdate();

                if (rows > 0) {
                    JOptionPane.showMessageDialog(dialog, "Details Added Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Email not found in database. Please register first.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        bottomPanel.add(submitBtn);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void openEditProfileWindow() {
        JDialog dialog = new JDialog(this, "Edit My Profile", true);
        dialog.setSize(450, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 15));
        formPanel.setBorder(new EmptyBorder(25, 30, 15, 30));
        formPanel.setBackground(Color.WHITE);

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField mobileField = new JTextField();
        JTextField companyField = new JTextField();
        JTextField designationField = new JTextField();
        JTextField ctcField = new JTextField();
        JTextField experienceField = new JTextField();

        formPanel.add(new JLabel("Full Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Email Address:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Mobile No:"));
        formPanel.add(mobileField);
        formPanel.add(new JLabel("Current Company:"));
        formPanel.add(companyField);
        formPanel.add(new JLabel("Designation:"));
        formPanel.add(designationField);
        formPanel.add(new JLabel("Package (CTC):"));
        formPanel.add(ctcField);
        formPanel.add(new JLabel("Experience:"));
        formPanel.add(experienceField);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        bottomPanel.setBackground(Color.WHITE);

        JButton saveBtn = new JButton("Submit");
        saveBtn.setPreferredSize(new Dimension(140, 38));
        saveBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        saveBtn.setBackground(new Color(41, 128, 185));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String mobile = mobileField.getText().trim();
            String company = companyField.getText().trim();
            String designation = designationField.getText().trim();
            String ctc = ctcField.getText().trim();
            String experience = experienceField.getText().trim();

            String sql = "UPDATE student SET name = ?, mobile = ?, company = ?, designation = ?, ctc = ?, experience = ? WHERE email = ?";
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setString(2, mobile);
                ps.setString(3, company);
                ps.setString(4, designation);
                ps.setString(5, ctc);
                ps.setString(6, experience);
                ps.setString(7, email);
                int rows = ps.executeUpdate();

                if (rows > 0) {
                    JOptionPane.showMessageDialog(dialog, "Profile Updated Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Email not found!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        bottomPanel.add(saveBtn);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void openRegisteredAlumniWindow() {
        JDialog dialog = new JDialog(this, "Registered Alumni", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        navBar.setBackground(new Color(240, 243, 244));

        JButton backBtn = new JButton("← Back to Dashboard");
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        backBtn.setBackground(new Color(52, 73, 94));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> dialog.dispose());

        navBar.add(backBtn);
        dialog.add(navBar, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);
        listPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Fetch Data from MySQL Database using ResultSet
        String sql = "SELECT name, email, mobile, company, designation, ctc, experience, address FROM student";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                final String[] person = {
                        rs.getString("name"),
                        "2023",
                        rs.getString("email"),
                        "N/A",
                        rs.getString("ctc"),
                        rs.getString("designation"),
                        rs.getString("experience"),
                        rs.getString("address") != null ? rs.getString("address") : "India",
                        "Database Registered Alumni Member."
                };

                JPanel itemCard = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
                itemCard.setBackground(new Color(245, 246, 250));
                itemCard.setMaximumSize(new Dimension(450, 60));
                itemCard.setBorder(BorderFactory.createLineBorder(new Color(220, 221, 225), 1));
                itemCard.setCursor(new Cursor(Cursor.HAND_CURSOR));

                JLabel iconLabel = new JLabel("👤");
                iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));

                JLabel nameLabel = new JLabel(person[0] != null ? person[0] : "Unknown");
                nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
                nameLabel.setForeground(new Color(44, 62, 80));

                itemCard.add(iconLabel);
                itemCard.add(nameLabel);

                itemCard.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        itemCard.setBackground(new Color(224, 247, 250));
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        itemCard.setBackground(new Color(245, 246, 250));
                    }

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        showAlumniDetailPopup(dialog, person);
                    }
                });

                listPanel.add(itemCard);
                listPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            }

            if (!hasData) {
                listPanel.add(new JLabel("No registered alumni found in database."));
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(dialog, "Error loading alumni data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void showAlumniDetailPopup(JDialog parent, String[] person) {
        JDialog popup = new JDialog(parent, person[0] + " - Profile Details", true);
        popup.setSize(420, 480);
        popup.setLocationRelativeTo(parent);
        popup.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        contentPanel.setBackground(Color.WHITE);

        JLabel photoLabel = new JLabel("👤", SwingConstants.CENTER);
        photoLabel.setFont(new Font("SansSerif", Font.PLAIN, 50));
        photoLabel.setPreferredSize(new Dimension(80, 80));
        photoLabel.setOpaque(true);
        photoLabel.setBackground(new Color(236, 240, 241));
        photoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel infoTextLabel = new JLabel("<html><center><i>" + person[8] + "</i></center></html>");
        infoTextLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        infoTextLabel.setForeground(Color.GRAY);
        infoTextLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea detailsArea = new JTextArea();
        detailsArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        detailsArea.setEditable(false);
        detailsArea.setBackground(new Color(250, 250, 250));
        detailsArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                new EmptyBorder(10, 10, 10, 10)
        ));

        detailsArea.setText(
                "Name: " + person[0] + "\n\n" +
                        "Batch: " + person[1] + "\n\n" +
                        "Email: " + person[2] + "\n\n" +
                        "CTC: " + person[4] + "\n\n" +
                        "Current Job: " + person[5] + "\n\n" +
                        "Experience: " + person[6] + "\n\n" +
                        "Address: " + person[7]
        );

        contentPanel.add(photoLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(infoTextLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(detailsArea);

        popup.add(new JScrollPane(contentPanel), BorderLayout.CENTER);
        popup.setVisible(true);
    }

    private JPanel createHoverCard(String title, String subtitle, Color baseColor, Color hoverColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(280, 100));
        card.setBackground(baseColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(178, 235, 242), 1),
                new EmptyBorder(12, 15, 12, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        titleLabel.setForeground(new Color(44, 62, 80));

        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subLabel.setForeground(new Color(84, 110, 122));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(subLabel, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(hoverColor);
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(baseColor);
            }
        });

        return card;
    }

    private JLabel createImageLabel(String relativePath, int width, int height) {
        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(width, height));
        label.setHorizontalAlignment(SwingConstants.CENTER);

        File file = new File(relativePath);
        if (file.exists()) {
            ImageIcon icon = new ImageIcon(relativePath);
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(img));
            return label;
        }

        URL resource = getClass().getResource("/pietlogo.png");
        if (resource != null) {
            ImageIcon icon = new ImageIcon(resource);
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(img));
            return label;
        }

        label.setOpaque(true);
        label.setBackground(new Color(200, 214, 229));
        label.setText("<html><center><b>[ Wide Banner Area ]</b><br>File not found at '" + relativePath + "'</center></html>");
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return label;
    }
}