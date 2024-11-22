package view;

import java.awt.Component;
import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import interface_adapter.change_password.ChangePasswordController;
import interface_adapter.profile.ProfileState;
import interface_adapter.profile.ProfileViewModel;
import interface_adapter.logout.LogoutController;

/**
 * The View for when the user is logged into the program.
 */
public class ProfileView extends JPanel implements PropertyChangeListener {

    private final String viewName = "profile";
    private final ProfileViewModel profileViewModel;
    private final JLabel passwordErrorField = new JLabel();
    private ChangePasswordController changePasswordController;
    private LogoutController logoutController;

    private final JLabel username;

    private final JButton logOut;
    private final JButton recipeSearch;
    private final JButton recipeSaved;

    private final JTextField passwordInputField = new JTextField(15);
    private final JButton changePassword;

    public ProfileView(ProfileViewModel profileViewModel) {
        this.profileViewModel = profileViewModel;
        this.profileViewModel.addPropertyChangeListener(this);

        final JLabel title = new JLabel("Profile");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        final LabelTextPanel passwordInfo = new LabelTextPanel(
                new JLabel("Password"), passwordInputField);

        final JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        final JLabel usernameInfo = new JLabel("'s account ");
        username = new JLabel();
        usernamePanel.add(username);
        usernamePanel.add(usernameInfo);

        final JPanel recipebuttons = new JPanel();
        recipeSearch = new JButton("Recipe Search");
        recipebuttons.add(recipeSearch);

        recipeSaved = new JButton("Saved Recipes");
        recipebuttons.add(recipeSaved);

        // Panel for Password input
        final JPanel passwordPanel = new JPanel();
        final JLabel passwordLabel = new JLabel("New Password");
        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.Y_AXIS));
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center-align label
        passwordInputField.setAlignmentX(Component.CENTER_ALIGNMENT); // Center-align text field
        passwordInputField.setMaximumSize(passwordInputField.getPreferredSize());
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordInputField);

        // Panel for Change Password button
        final JPanel changePasswordPanel = new JPanel();
        changePasswordPanel.setLayout(new BoxLayout(changePasswordPanel, BoxLayout.Y_AXIS));
        changePassword = new JButton("Change Password");
        changePassword.setAlignmentX(Component.CENTER_ALIGNMENT);
        changePasswordPanel.add(changePassword);

        // Panel for Log Out button
        final JPanel logOutPanel = new JPanel();
        logOutPanel.setLayout(new BoxLayout(logOutPanel, BoxLayout.Y_AXIS));
        logOut = new JButton("Log Out");
        logOut.setAlignmentX(Component.CENTER_ALIGNMENT);
        logOutPanel.add(logOut);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        passwordInputField.getDocument().addDocumentListener(new DocumentListener() {

            private void documentListenerHelper() {
                final ProfileState currentState = profileViewModel.getState();
                currentState.setPassword(passwordInputField.getText());
                profileViewModel.setState(currentState);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                documentListenerHelper();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                documentListenerHelper();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                documentListenerHelper();
            }
        });

        changePassword.addActionListener(
                // This creates an anonymous subclass of ActionListener and instantiates it.
                evt -> {
                    if (evt.getSource().equals(changePassword)) {
                        final ProfileState currentState = profileViewModel.getState();

                        this.changePasswordController.execute(
                                currentState.getUsername(),
                                currentState.getPassword()
                        );
                    }
                }
        );

        logOut.addActionListener(
                // This creates an anonymous subclass of ActionListener and instantiates it.
                evt -> {
                    if (evt.getSource().equals(logOut)) {
                        // 1. get the state out of the loggedInViewModel. It contains the username.
                        final ProfileState profileState = profileViewModel.getState();
                        // 2. Execute the logout Controller.
                        this.logoutController.execute(profileState.getUsername());
                    }
                }
        );

        this.add(title);
        this.add(usernamePanel);
        this.add(recipebuttons);
        this.add(passwordPanel);
        this.add(passwordErrorField);
        this.add(changePasswordPanel);
        this.add(logOutPanel);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("state")) {
            final ProfileState state = (ProfileState) evt.getNewValue();
            username.setText(state.getUsername());
        } else if (evt.getPropertyName().equals("password")) {
            final ProfileState state = (ProfileState) evt.getNewValue();
            JOptionPane.showMessageDialog(null, "password updated for " + state.getUsername());
        }

    }

    public String getViewName() {
        return viewName;
    }

    public void setChangePasswordController(ChangePasswordController changePasswordController) {
        this.changePasswordController = changePasswordController;
    }

    public void setLogoutController(LogoutController logoutController) {
        this.logoutController = logoutController;
    }
}