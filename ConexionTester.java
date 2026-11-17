import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;

public class ConexionTester extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final String URL_POR_DEFECTO = "jdbc:sqlite:inventario.db";
    private static final Color VERDE = new Color(0, 128, 0);
    private static final Color ROJO = new Color(192, 0, 0);

    private final JTextField campoUrl = new JTextField(URL_POR_DEFECTO, 30);
    private final JTextField campoUsuario = new JTextField(30);
    private final JPasswordField campoContrasena = new JPasswordField(30);
    private final JButton botonProbar = new JButton("Probar conexión");
    private final JTextArea areaResultado = new JTextArea(8, 40);

    public ConexionTester() {
        super("Probador de conexión JDBC");
        construirInterfaz();
    }

    private void construirInterfaz() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panelPrincipal.add(crearPanelFormulario(), BorderLayout.NORTH);
        panelPrincipal.add(crearPanelResultado(), BorderLayout.CENTER);
        setContentPane(panelPrincipal);

        botonProbar.addActionListener(new ProbarConexionListener());

        pack();
        setMinimumSize(new Dimension(520, 380));
        setLocationRelativeTo(null);
    }

    private JPanel crearPanelFormulario() {
        JPanel campos = new JPanel(new GridLayout(3, 2, 10, 10));
        campos.add(new JLabel("URL de conexión:"));
        campos.add(campoUrl);
        campos.add(new JLabel("Usuario (vacío si es SQLite):"));
        campos.add(campoUsuario);
        campos.add(new JLabel("Contraseña (vacía si es SQLite):"));
        campos.add(campoContrasena);

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBoton.add(botonProbar);

        JPanel formulario = new JPanel(new BorderLayout(0, 10));
        formulario.add(campos, BorderLayout.CENTER);
        formulario.add(panelBoton, BorderLayout.SOUTH);
        return formulario;
    }

    private JScrollPane crearPanelResultado() {
        areaResultado.setEditable(false);
        areaResultado.setLineWrap(true);
        areaResultado.setWrapStyleWord(true);
        areaResultado.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        JScrollPane scroll = new JScrollPane(areaResultado);
        scroll.setBorder(BorderFactory.createTitledBorder("Resultado de la prueba"));
        return scroll;
    }

    private class ProbarConexionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            probarConexion();
        }
    }

    private void probarConexion() {
        String url = campoUrl.getText().trim();
        String usuario = campoUsuario.getText().trim();
        char[] clave = campoContrasena.getPassword();
        String contrasena = new String(clave);
        Arrays.fill(clave, ' ');

        if (url.isEmpty()) {
            mostrarResultado("Debe ingresar la URL de conexión.", ROJO);
            return;
        }

        try {
            String claseDriver = obtenerClaseDriver(url);
            if (claseDriver != null) {
                Class.forName(claseDriver);
            }

            try (Connection conexion = abrirConexion(url, usuario, contrasena)) {
                DatabaseMetaData meta = conexion.getMetaData();
                mostrarResultado("Conexión exitosa.\n\n"
                        + "URL: " + url + "\n"
                        + "Motor: " + meta.getDatabaseProductName()
                        + " " + meta.getDatabaseProductVersion(), VERDE);
            }
        } catch (ClassNotFoundException e) {
            mostrarResultado("Fallo del driver: no se encontró la clase "
                    + e.getMessage() + ".\n"
                    + "Agregue el .jar del driver JDBC a las librerías del proyecto.", ROJO);
        } catch (SQLException e) {
            mostrarResultado("Fallo de conexión.\n\n"
                    + "Mensaje técnico: " + e.getMessage() + "\n"
                    + "SQLState: " + e.getSQLState()
                    + " | Código de error: " + e.getErrorCode(), ROJO);
        }
    }

    private Connection abrirConexion(String url, String usuario, String contrasena)
            throws SQLException {
        if (usuario.isEmpty() && contrasena.isEmpty()) {
            return DriverManager.getConnection(url);
        }
        return DriverManager.getConnection(url, usuario, contrasena);
    }

    private String obtenerClaseDriver(String url) {
        String u = url.toLowerCase();
        if (u.startsWith("jdbc:sqlite:")) {
            return "org.sqlite.JDBC";
        } else if (u.startsWith("jdbc:mysql:")) {
            return "com.mysql.cj.jdbc.Driver";
        } else if (u.startsWith("jdbc:postgresql:")) {
            return "org.postgresql.Driver";
        } else if (u.startsWith("jdbc:sqlserver:")) {
            return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        }
        return null;
    }

    private void mostrarResultado(String mensaje, Color color) {
        areaResultado.setForeground(color);
        areaResultado.setText(mensaje);
        areaResultado.setCaretPosition(0);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignorada) {
            // Si falla, se usa el aspecto por defecto de Swing.
        }
        SwingUtilities.invokeLater(() -> new ConexionTester().setVisible(true));
    }
}
