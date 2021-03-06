/*
 * This file is part of The Technic Launcher Version 3.
 * Copyright ©2015 Syndicate, LLC
 *
 * The Technic Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Technic Launcher  is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Technic Launcher.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.technicpack.launcher.ui.components;

import net.littlebits.ui.BitcraftPanel;
import net.technicpack.autoupdate.IBuildNumber;
import net.technicpack.launcher.LauncherMain;
import net.technicpack.launcher.settings.StartupParameters;
import net.technicpack.launcher.ui.InstallerFrame;
import net.technicpack.minecraftcore.launch.WindowType;
import net.technicpack.ui.listitems.javaversion.Best64BitVersionItem;
import net.technicpack.ui.listitems.javaversion.DefaultVersionItem;
import net.technicpack.ui.listitems.javaversion.JavaVersionItem;
import net.technicpack.launchercore.launch.java.IJavaVersion;
import net.technicpack.launchercore.launch.java.JavaVersionRepository;
import net.technicpack.launchercore.launch.java.source.FileJavaSource;
import net.technicpack.launchercore.launch.java.version.FileBasedJavaVersion;
import net.technicpack.ui.controls.TooltipWarning;
import net.technicpack.ui.controls.lang.LanguageCellRenderer;
import net.technicpack.ui.controls.list.SimpleButtonComboUI;
import net.technicpack.ui.controls.list.popupformatters.RoundedBorderFormatter;
import net.technicpack.ui.lang.IRelocalizableResource;
import net.technicpack.ui.lang.ResourceLoader;
import net.technicpack.launcher.settings.TechnicSettings;
import net.technicpack.launcher.ui.LauncherFrame;
import net.technicpack.ui.controls.LauncherDialog;
import net.technicpack.ui.controls.RoundedButton;
import net.technicpack.ui.controls.borders.RoundBorder;
import net.technicpack.ui.controls.tabs.SimpleTabPane;
import net.technicpack.ui.listitems.LanguageItem;
import net.technicpack.launcher.ui.listitems.OnLaunchItem;
import net.technicpack.launcher.ui.listitems.StreamItem;
import net.technicpack.launchercore.util.LaunchAction;
import net.technicpack.utilslib.DesktopUtils;
import net.technicpack.utilslib.Memory;
import net.technicpack.utilslib.OperatingSystem;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.metal.MetalComboBoxUI;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Locale;

public class OptionsDialog extends LauncherDialog implements IRelocalizableResource {

    private static final int DIALOG_WIDTH = 830;
    private static final int DIALOG_HEIGHT = 564;

    private TechnicSettings settings;

    private boolean hasShownStreamInfo = false;
    private ResourceLoader resources;
    private final JavaVersionRepository javaVersions;
    private final FileJavaSource fileJavaSource;
    private final IBuildNumber buildNumber;

    public static final Color COLOR_LITTLEBITS_ORANGE = new Color(255,138,0); // #FF8A00
    public static final Color COLOR_LITTLEBITS_BUTTON_HOVER = new Color(230,140,0); // #e67c00
    public static final Color COLOR_LITTLEBITS_WHITE = new Color(255,255,255);
    public static final Color COLOR_LITTLEBITS_TEXT = new Color(68,68,68); // #444
    public static final Color COLOR_LITTLEBITS_TEXT_FIELD_BG = new Color(238,238,238); // #eee
    public static final Color COLOR_LITTLEBITS_TEXT_FIELD_STROKE = new Color(136,136,136); // #ccc
    public static final Color COLOR_LITTLEBITS_GREY_BG = new Color(238,238,238); // #eee
    public static final Color COLOR_LITTLEBITS_PURPLE = new Color(95,2,126);
    public static final Color COLOR_WHITE = new Color(255,255,255);

    public static final Color COLORS_FOOTER = new Color(238,238,238);//COLOR_LITTLEBITS_TEXT_FIELD_BG;
    public static final Color COLORS_GREY_TEXT = new Color(68,68,68);//COLOR_LITTLEBITS_TEXT;
    public static final Color COLORS_WHITE_TEXT = new Color(68,68,68);//COLOR_LITTLEBITS_TEXT;
    public static final Color COLORS_FORMELEMENT_INTERNAL = new Color(255,255,255);//new Color(238,238,238);//COLOR_LITTLEBITS_GREY_BG;
    public static final Color COLORS_BLUE = COLOR_LITTLEBITS_PURPLE;
    public static final Color COLORS_CENTRAL_BACK_OPAQUE = new Color(238,238,238);//COLOR_LITTLEBITS_TEXT_FIELD_BG;
    public static final Color COLORS_BUTTON_BLUE = COLOR_LITTLEBITS_PURPLE;
    public static final Color COLORS_SCROLL_TRACK = new Color(238,238,238);//COLOR_LITTLEBITS_GREY_BG;
    public static final Color COLORS_SCROLL_THUMB = COLOR_LITTLEBITS_PURPLE;

    private DocumentListener javaArgsListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            changeJavaArgs();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            changeJavaArgs();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            changeJavaArgs();
        }
    };

    private DocumentListener dimensionListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            changeWindowDimensions();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            changeWindowDimensions();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            changeWindowDimensions();
        }
    };

    JComboBox versionSelect;
    JComboBox memSelect;
    JTextArea javaArgs;
    JComboBox streamSelect;
    JComboBox launchSelect;
    JComboBox langSelect;
    JTextField installField;
    JTextField clientId;
    JCheckBox showConsole;
    JCheckBox launchToModpacks;
    StartupParameters params;
    Component ramWarning;
    JCheckBox askFirstBox;
    JComboBox useStencil;
    JComboBox windowSelect;
    JTextField widthInput;
    JTextField heightInput;

    public OptionsDialog(final Frame owner, final TechnicSettings settings, final ResourceLoader resourceLoader, final StartupParameters params, final JavaVersionRepository javaVersions, final FileJavaSource fileJavaSource, final IBuildNumber buildNumber) {
        super(owner);

        this.settings = settings;
        this.params = params;
        this.javaVersions = javaVersions;
        this.fileJavaSource = fileJavaSource;
        this.buildNumber = buildNumber;

        relocalize(resourceLoader);
    }

    protected void closeDialog() {
        resources.unregisterResource(this);
        dispose();
    }

    protected void changeJavaArgs() {
        settings.setJavaArgs(javaArgs.getText());
        settings.save();
    }

    protected void copyCid() {
        StringSelection selection = new StringSelection(clientId.getText());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
    }

    protected void changeShowConsole() {
        settings.setShowConsole(showConsole.isSelected());
        LauncherMain.consoleFrame.setVisible(showConsole.isSelected());
        settings.save();
    }

    protected void changeAskFirst() {
        settings.setAutoAcceptModpackRequirements(!askFirstBox.isSelected());
        settings.save();
    }

    protected void changeLaunchToModpacks() {
        settings.setLaunchToModpacks(launchToModpacks.isSelected());
        settings.save();
    }

    protected void changeJavaVersion() {
        String version = ((JavaVersionItem)versionSelect.getSelectedItem()).getVersionNumber();
        boolean is64 = ((JavaVersionItem)versionSelect.getSelectedItem()).is64Bit();
        javaVersions.selectVersion(version, is64);
        settings.setJavaVersion(version);
        settings.setJavaBitness(is64);
        settings.save();
        rebuildMemoryList();
    }

    protected void selectOtherVersion() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        final String osJavaName = new File(OperatingSystem.getJavaDir()).getName();
        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory())
                    return true;
                return (f.getName().equals(osJavaName));
            }

            @Override
            public String getDescription() {
                return resources.getString("launcheroptions.java.filter", osJavaName);
            }
        });

        int result = chooser.showOpenDialog(this);


        if (result == JFileChooser.APPROVE_OPTION) {
            if (chooser.getSelectedFile() == null || !chooser.getSelectedFile().exists() || !chooser.getSelectedFile().canExecute()) {
                JOptionPane.showMessageDialog(this, resources.getString("launcheroptions.java.badfile"));
                return;
            }

            FileBasedJavaVersion chosenJava = new FileBasedJavaVersion(chooser.getSelectedFile());
            if (!chosenJava.verify()) {
                JOptionPane.showMessageDialog(this, resources.getString("launcheroptions.java.badfile"));
                return;
            }

            IJavaVersion existingVersion = javaVersions.getVersion(chosenJava.getVersionNumber(), chosenJava.is64Bit());
            if (existingVersion.getJavaPath() != null) {
                JOptionPane.showMessageDialog(this, resources.getString("launcheroptions.java.versionexists"));
                return;
            }

            fileJavaSource.addVersion(chosenJava);
            javaVersions.addVersion(chosenJava);
            javaVersions.selectVersion(chosenJava.getVersionNumber(), chosenJava.is64Bit());
            JavaVersionItem item = new JavaVersionItem(chosenJava, resources);
            versionSelect.addItem(item);
            versionSelect.setSelectedItem(item);
            settings.setJavaVersion(chosenJava.getVersionNumber());
            settings.setJavaBitness(chosenJava.is64Bit());
            settings.save();
        }
    }

    protected void changeMemory() {
        settings.setMemory(((Memory) memSelect.getSelectedItem()).getSettingsId());
        settings.save();
    }

    protected void changeStream() {
        settings.setBuildStream(((StreamItem) streamSelect.getSelectedItem()).getStream());
        settings.save();

        if (!hasShownStreamInfo) {
            JOptionPane.showMessageDialog(this, resources.getString("launcheroptions.streamchange.text"), resources.getString("launcheroptions.streamchange.title"), JOptionPane.INFORMATION_MESSAGE);

            hasShownStreamInfo = true;
        }
    }

    protected void changeLaunchAction() {
        settings.setLaunchAction(((OnLaunchItem) launchSelect.getSelectedItem()).getLaunchAction());
        settings.save();
    }

    protected void changeLanguage() {
        settings.setLanguageCode(((LanguageItem) langSelect.getSelectedItem()).getLangCode());
        settings.save();

        resources.setLocale(((LanguageItem) langSelect.getSelectedItem()).getLangCode());
    }

    protected void changeWindowType() {
        switch(windowSelect.getSelectedIndex()) {
            case 0:
                settings.setLaunchWindowType(WindowType.DEFAULT);
                break;
            case 1:
                settings.setLaunchWindowType(WindowType.FULLSCREEN);
                break;
            case 2:
                settings.setLaunchWindowType(WindowType.CUSTOM);
                changeWindowDimensions();
                break;
        }

        updateDimensionsEnabled();
    }

    protected void changeEnableStencil() {
        settings.setUseStencilBuffer(useStencil.getSelectedIndex() == 0);
    }

    protected void changeWindowDimensions() {
        String widthStr = widthInput.getText();
        String heightStr = heightInput.getText();
        int width = 800;
        int height = 600;

        try {
            width = Integer.parseInt(widthStr);
        } catch (NumberFormatException ex) {
            //Not important
        }

        try {
            height = Integer.parseInt(heightStr);
        } catch (NumberFormatException ex) {
            //Not important
        }

        settings.setLaunchWindowDimensions(width, height);
    }

    protected void reinstall() {
        final InstallerFrame frame = new InstallerFrame(resources, params, settings, getOwner());
        frame.setVisible(true);

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                frame.requestFocus();
            }
        });

        this.dispose();
    }

    protected void openLogs() {
        DesktopUtils.open(new File(settings.getTechnicRoot().getAbsolutePath(), "logs"));
    }

    private void initControlValues() {

        javaArgs.getDocument().removeDocumentListener(javaArgsListener);
        javaArgs.setText(settings.getJavaArgs());
        javaArgs.getDocument().addDocumentListener(javaArgsListener);

        installField.setText(settings.getTechnicRoot().getAbsolutePath());
        clientId.setText(settings.getClientId());

        for (ActionListener listener : showConsole.getActionListeners())
            showConsole.removeActionListener(listener);
        showConsole.setSelected(settings.getShowConsole());
        showConsole.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeShowConsole();
            }
        });

        for (ActionListener listener : askFirstBox.getActionListeners())
            askFirstBox.removeActionListener(listener);
        askFirstBox.setSelected(!settings.shouldAutoAcceptModpackRequirements());
        askFirstBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeAskFirst();
            }
        });

        for (ActionListener listener : launchToModpacks.getActionListeners())
            launchToModpacks.removeActionListener(listener);
        launchToModpacks.setSelected(settings.getLaunchToModpacks());
        launchToModpacks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeLaunchToModpacks();
            }
        });

        for (ActionListener listener : versionSelect.getActionListeners())
            versionSelect.removeActionListener(listener);

        versionSelect.removeAllItems();
        versionSelect.addItem(new DefaultVersionItem(javaVersions.getVersion(null, true), resources));

        IJavaVersion best64Bit = javaVersions.getBest64BitVersion();
        if (best64Bit != null)
            versionSelect.addItem(new Best64BitVersionItem(javaVersions.getVersion("64bit", true), resources));

        for (IJavaVersion version : javaVersions.getVersions()) {
            versionSelect.addItem(new JavaVersionItem(version, resources));
        }

        String settingsVersion = settings.getJavaVersion();
        boolean settingsBitness = settings.getJavaBitness();
        if (settingsVersion == null || settingsVersion.isEmpty() || settingsVersion.equals("default"))
            versionSelect.setSelectedIndex(0);
        else if (settingsVersion.equals("64bit"))
            versionSelect.setSelectedIndex(1);
        else {
            for (int i = 2; i < versionSelect.getItemCount(); i++) {
                if (((JavaVersionItem)versionSelect.getItemAt(i)).getVersionNumber().equals(settingsVersion) && ((JavaVersionItem)versionSelect.getItemAt(i)).is64Bit() == settingsBitness) {
                    versionSelect.setSelectedIndex(i);
                    break;
                }
            }
        }

        versionSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeJavaVersion();
            }
        });

        rebuildMemoryList();

        for (ActionListener listener : streamSelect.getActionListeners()) {
            streamSelect.removeActionListener(listener);
        }
        streamSelect.removeAllItems();
        streamSelect.addItem(new StreamItem(resources.getString("launcheroptions.build.stable"), "stable"));
        streamSelect.addItem(new StreamItem(resources.getString("launcheroptions.build.beta"), "beta"));
        streamSelect.setSelectedIndex((settings.getBuildStream().equals("beta"))?1:0);
        streamSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeStream();
            }
        });

        for (ActionListener listener : launchSelect.getActionListeners())
            launchSelect.removeActionListener(listener);
        launchSelect.removeAllItems();
        launchSelect.addItem(new OnLaunchItem(resources.getString("launcheroptions.packlaunch.hide"), LaunchAction.HIDE));
        launchSelect.addItem(new OnLaunchItem(resources.getString("launcheroptions.packlaunch.close"), LaunchAction.CLOSE));
        launchSelect.addItem(new OnLaunchItem(resources.getString("launcheroptions.packlaunch.nothing"), LaunchAction.NOTHING));

        switch (settings.getLaunchAction()) {
            case HIDE:
                launchSelect.setSelectedIndex(0);
                break;
            case CLOSE:
                launchSelect.setSelectedIndex(1);
                break;
            case NOTHING:
                launchSelect.setSelectedIndex(2);
                break;
            default:
                launchSelect.setSelectedIndex(0);
        }
        launchSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeLaunchAction();
            }
        });

        for (ActionListener listener : langSelect.getActionListeners())
            langSelect.removeActionListener(listener);
        langSelect.removeAllItems();

        String defaultLocaleText = resources.getString("launcheroptions.language.default");
        if (!resources.isDefaultLocaleSupported()) {
            defaultLocaleText = defaultLocaleText.concat(" (" + resources.getString("launcheroptions.language.unavailable") + ")");
        }

        langSelect.setRenderer(new LanguageCellRenderer(resources, null, langSelect.getBackground(), langSelect.getForeground()));
        langSelect.addItem(new LanguageItem(ResourceLoader.DEFAULT_LOCALE, defaultLocaleText, resources));
        for (int i = 0; i < LauncherMain.supportedLanguages.length; i++) {
            langSelect.addItem(new LanguageItem(resources.getCodeFromLocale(LauncherMain.supportedLanguages[i]), LauncherMain.supportedLanguages[i].getDisplayName(LauncherMain.supportedLanguages[i]), resources.getVariant(LauncherMain.supportedLanguages[i])));
        }
        if (!settings.getLanguageCode().equalsIgnoreCase(ResourceLoader.DEFAULT_LOCALE)) {
            Locale loc = resources.getLocaleFromCode(settings.getLanguageCode());

            for (int i = 0; i < LauncherMain.supportedLanguages.length; i++) {
                if (loc.equals(LauncherMain.supportedLanguages[i])) {
                    langSelect.setSelectedIndex(i+1);
                    break;
                }
            }
        }
        langSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeLanguage();
            }
        });

        widthInput.getDocument().removeDocumentListener(dimensionListener);
        heightInput.getDocument().removeDocumentListener(dimensionListener);
        int width = settings.getCustomWidth();
        int height = settings.getCustomHeight();

        width = (width<1)?800:width;
        height = (height<1)?600:height;
        widthInput.setText(Integer.toString(width));
        heightInput.setText(Integer.toString(height));
        widthInput.getDocument().addDocumentListener(dimensionListener);
        heightInput.getDocument().addDocumentListener(dimensionListener);

        for (ActionListener listener : windowSelect.getActionListeners()) {
            windowSelect.removeActionListener(listener);
        }
        windowSelect.removeAllItems();
        windowSelect.addItem(resources.getString("launcheroptions.video.windowSize.default"));
        windowSelect.addItem(resources.getString("launcheroptions.video.windowSize.fullscreen"));
        windowSelect.addItem(resources.getString("launcheroptions.video.windowSize.custom"));
        switch (settings.getLaunchWindowType()) {
            case DEFAULT:
                windowSelect.setSelectedIndex(0);
                break;
            case FULLSCREEN:
                windowSelect.setSelectedIndex(1);
                break;
            case CUSTOM:
                windowSelect.setSelectedIndex(2);
                break;
        }
        windowSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeWindowType();
            }
        });
        updateDimensionsEnabled();

        for (ActionListener listener : useStencil.getActionListeners()) {
            useStencil.removeActionListener(listener);
        }
        useStencil.removeAllItems();
        useStencil.addItem(resources.getString("launcheroptions.video.stencil.enabled"));
        useStencil.addItem(resources.getString("launcheroptions.video.stencil.disabled"));
        if (settings.shouldUseStencilBuffer())
            useStencil.setSelectedIndex(0);
        else
            useStencil.setSelectedIndex(1);
        useStencil.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeEnableStencil();
            }
        });
    }

    private void rebuildMemoryList() {
        for (ActionListener listener : memSelect.getActionListeners())
            memSelect.removeActionListener(listener);

        Container parent = null;
        if (memSelect.getParent() != null) {
            parent = memSelect.getParent();
            parent.remove(memSelect);

            if (ramWarning != null) {
                parent.remove(ramWarning);
                ramWarning = null;
            }
        }

        memSelect.removeAllItems();
        long maxMemory = Memory.getAvailableMemory(javaVersions.getSelectedVersion().is64Bit());
        for (int i = 0; i < Memory.memoryOptions.length; i++) {
            if (Memory.memoryOptions[i].getMemoryMB() <= maxMemory)
                memSelect.addItem(Memory.memoryOptions[i]);
        }

        Memory currentMem = Memory.getMemoryFromId(settings.getMemory());
        Memory availableMem = Memory.getClosestAvailableMemory(currentMem, javaVersions.getSelectedVersion().is64Bit());

        if (currentMem.getMemoryMB() != availableMem.getMemoryMB()) {
            settings.setMemory(availableMem.getSettingsId());
            settings.save();
        }
        memSelect.setSelectedItem(availableMem);
        memSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeMemory();
            }
        });

        if (parent != null) {
            boolean is64Bit = true;
            boolean has64Bit = true;
            if (javaVersions.getBest64BitVersion() == null) {
                has64Bit = false;
            }

            if (!javaVersions.getSelectedVersion().is64Bit()) {
                is64Bit = false;
            }

            if (is64Bit) {
                parent.add(memSelect, new GridBagConstraints(1, 1, 6, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(8, 16, 8, 80), 0, 16));
            } else {
                parent.add(memSelect, new GridBagConstraints(1, 1, 5, 1, 5, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(8, 16, 8, 0), 0, 16));

                JToolTip toolTip = new JToolTip();
                toolTip.setBackground(COLORS_FOOTER);
                toolTip.setForeground(COLORS_GREY_TEXT);
                toolTip.setBorder(BorderFactory.createCompoundBorder(new LineBorder(COLORS_GREY_TEXT), BorderFactory.createEmptyBorder(5,5,5,5)));
                toolTip.setFont(BitcraftPanel.mainFont.deriveFont(Font.PLAIN, 14));


                String text = null;
                Icon icon = null;

                if (has64Bit) {
                    text = resources.getString("launcheroptions.java.use64bit");
                    icon = resources.getIcon("danger_icon.png");
                } else {
                    text = resources.getString("launcheroptions.java.get64bit");
                    icon = resources.getIcon("warning_icon.png");
                }

                ramWarning = new TooltipWarning(icon, toolTip);
                ((TooltipWarning)ramWarning).setToolTipText(text);
                parent.add(ramWarning, new GridBagConstraints(6, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(8,8,8,80),0,0));
            }
            repaint();
        }
    }

    private void initComponents() {
        setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        setLayout(new BorderLayout());

        JPanel header = new JPanel();
        header.setBackground(new Color(238,238,238)); //Header background color
        header.setLayout(new BoxLayout(header, BoxLayout.LINE_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        add(header, BorderLayout.PAGE_START);

        JLabel title = new JLabel(resources.getString("launcher.title.options"));
        title.setFont(BitcraftPanel.mainFont.deriveFont(Font.PLAIN, 26));
        title.setForeground(COLORS_WHITE_TEXT);
        title.setOpaque(false);
        title.setIcon(resources.getIcon("options_cog.png"));
        title.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        header.add(title);

        header.add(Box.createHorizontalGlue());

        JButton closeButton = new JButton();
        closeButton.setIcon(resources.getIcon("close-black.png"));
        closeButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        closeButton.setContentAreaFilled(false);
        closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeDialog();
            }
        });
        header.add(closeButton);

        SimpleTabPane centerPanel = new SimpleTabPane();
        centerPanel.setBackground(COLOR_LITTLEBITS_PURPLE);
        centerPanel.setForeground(new Color(255,255,255));
        centerPanel.setSelectedBackground(new Color(255,255,255));
        centerPanel.setSelectedForeground(COLOR_LITTLEBITS_PURPLE);
        centerPanel.setFont(BitcraftPanel.mainFont.deriveFont(Font.PLAIN, 14));
        centerPanel.setOpaque(true);
        add(centerPanel, BorderLayout.CENTER);

        JPanel general = new JPanel();
        general.setBackground(COLORS_CENTRAL_BACK_OPAQUE);

        setupGeneralPanel(general);

        JPanel javaOptions = new JPanel();
        javaOptions.setBackground(COLORS_CENTRAL_BACK_OPAQUE);

        setupJavaOptionsPanel(javaOptions);

        JPanel videoOptions = new JPanel();
        videoOptions.setBackground(COLORS_CENTRAL_BACK_OPAQUE);

        setupVideoOptionsPanel(videoOptions);

        JPanel about = new JPanel();
        about.setBackground(COLORS_CENTRAL_BACK_OPAQUE);

        String linkText = "<a href=\"https://github.com/TechnicPack/\">"+resources.getString("launcheroptions.about.linktext")+"</a>";
        String aboutText = "<html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"http://www.technicpack.net/assets/css/launcher.css\" /></head><body style=\"font-family: "+BitcraftPanel.mainFont.deriveFont(Font.PLAIN, 12).getFamily()+";color:#444444\">";
        aboutText += "<p>" + resources.getString("launcheroptions.about.copyright", buildNumber.getBuildNumber(), linkText) + "</p>";
        aboutText += "<p>" + resources.getString("launcheroptions.about.romainguy") + "</p>";
        aboutText += "<p>" + resources.getString("launcheroptions.about.summary") + "</p>";

        about.setLayout(new BorderLayout());

        JLabel buildCtrl = new JLabel(resources.getString("launcher.build.text", buildNumber.getBuildNumber(), resources.getString("launcher.build." + settings.getBuildStream())));
        buildCtrl.setForeground(COLORS_WHITE_TEXT);
        buildCtrl.setFont(BitcraftPanel.mainFont.deriveFont(Font.PLAIN, 14));
        buildCtrl.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 0));
        about.add(buildCtrl, BorderLayout.SOUTH);

        JTextPane textPane = new JTextPane();
        textPane.setBorder(BorderFactory.createEmptyBorder(0, 24, 9, 24));
        textPane.setOpaque(false);
        textPane.setForeground(COLORS_WHITE_TEXT);
        textPane.setFont(BitcraftPanel.mainFont.deriveFont(Font.PLAIN, 16));
        textPane.setEditable(false);
        textPane.setHighlighter(null);
        textPane.setAlignmentX(LEFT_ALIGNMENT);
        textPane.setContentType("text/html");
        textPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
                    DesktopUtils.browseUrl(e.getURL().toString());
            }
        });
        MutableAttributeSet attributes = new SimpleAttributeSet(textPane.getParagraphAttributes());
        StyleConstants.setLineSpacing(attributes, StyleConstants.getLineSpacing(attributes) * 1.3f);
        textPane.setParagraphAttributes(attributes, true);

        textPane.setText(aboutText);
        about.add(textPane, BorderLayout.CENTER);

        centerPanel.addTab(resources.getString("launcheroptions.tab.general").toUpperCase(), general);
        centerPanel.addTab(resources.getString("launcheroptions.tab.java").toUpperCase(), javaOptions);
        centerPanel.addTab(resources.getString("launcheroptions.tab.video").toUpperCase(), videoOptions);
        centerPanel.addTab(resources.getString("launcheroptions.tab.about").toUpperCase(), about);
        centerPanel.setFocusable(false);
    }

    private void setupGeneralPanel(JPanel panel) {

        panel.setLayout(new GridBagLayout());

        JLabel streamLabel = new JLabel(resources.getString("launcheroptions.general.build"));
        streamLabel.setFont(BitcraftPanel.mainFont.deriveFont(Font.PLAIN, 16));
        streamLabel.setForeground(COLORS_WHITE_TEXT);
        panel.add(streamLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 40, 0, 0), 0, 0));

        // Setup stream select box
        streamSelect = new JComboBox();

        if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("mac")) {
            streamSelect.setUI(new MetalComboBoxUI());
        }

        streamSelect.setFont(BitcraftPanel.mainFont.deriveFont(Font.PLAIN, 16));
        streamSelect.setEditable(false);
        streamSelect.setBorder(new RoundBorder(COLORS_BUTTON_BLUE, 1, 10));
        streamSelect.setForeground(COLORS_BUTTON_BLUE);
        streamSelect.setBackground(COLORS_FORMELEMENT_INTERNAL);
        streamSelect.setUI(new SimpleButtonComboUI(new RoundedBorderFormatter(new RoundBorder(COLORS_BUTTON_BLUE, 1, 4)), resources, COLORS_SCROLL_TRACK, COLORS_SCROLL_THUMB));
        streamSelect.setFocusable(false);

        Object child = streamSelect.getAccessibleContext().getAccessibleChild(0);
        BasicComboPopup popup = (BasicComboPopup)child;
        JList list = popup.getList();
        list.setSelectionForeground(COLORS_BUTTON_BLUE);
        list.setSelectionBackground(COLORS_FORMELEMENT_INTERNAL);
        list.setBackground(COLORS_CENTRAL_BACK_OPAQUE);

        panel.add(streamSelect, new GridBagConstraints(1, 0, 2, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(8, 16, 8, 16), 0, 16));

        //Setup language box
        JLabel langLabel = new JLabel(resources.getString("launcheroptions.general.lang"));
        langLabel.setFont(BitcraftPanel.mainFont.deriveFont(Font.PLAIN, 16));
        langLabel.setForeground(COLORS_WHITE_TEXT);
        panel.add(langLabel, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 40, 0, 0), 0, 0));

        langSelect = new JComboBox();

        if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("mac")) {
            langSelect.setUI(new MetalComboBoxUI());
        }

        langSelect.setFont(BitcraftPanel.mainFont.deriveFont(Font.PLAIN, 16));
        langSelect.setEditable(false);
        langSelect.setBorder(new RoundBorder(COLORS_BUTTON_BLUE, 1, 10));
        langSelect.setForeground(COLORS_BUTTON_BLUE);
        langSelect.setBackground(COLORS_FORMELEMENT_INTERNAL);
        langSelect.setUI(new SimpleButtonComboUI(new RoundedBorderFormatter(new RoundBorder(COLORS_BUTTON_BLUE, 1, 4)), resources, COLORS_SCROLL_TRACK, COLORS_SCROLL_THUMB));
        langSelect.setFocusable(false);

        child = langSelect.getAccessibleContext().getAccessibleChild(0);
        popup = (BasicComboPopup)child;
        list = popup.getList();
        list.setSelectionForeground(COLORS_BUTTON_BLUE);
        list.setSelectionBackground(COLORS_FORMELEMENT_INTERNAL);
        list.setBackground(COLORS_CENTRAL_BACK_OPAQUE);

        panel.add(langSelect, new GridBagConstraints(1, 1, 2, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(8, 16, 8, 16), 0, 16));

        //Setup on pack launch box
        JLabel launchLabel = new JLabel(resources.getString("launcheroptions.general.onlaunch"));
        launchLabel.setFont(BitcraftPanel.mainFont.deriveFont(Font.PLAIN, 16));
        launchLabel.setForeground(COLORS_WHITE_TEXT);
        panel.add(launchLabel, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 40, 0, 0), 0, 0));

        launchSelect = new JComboBox();

        if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("mac")) {
            launchSelect.setUI(new MetalComboBoxUI());
        }

        launchSelect.setFont(BitcraftPanel.mainFont.deriveFont(Font.PLAIN, 16));
        launchSelect.setEditable(false);
        launchSelect.setBorder(new RoundBorder(COLORS_BUTTON_BLUE, 1, 10));
        launchSelect.setForeground(COLORS_BUTTON_BLUE);
        launchSelect.setBackground(COLORS_FORMELEMENT_INTERNAL);
        launchSelect.setUI(new SimpleButtonComboUI(new RoundedBorderFormatter(new RoundBorder(COLORS_BUTTON_BLUE, 1, 4)), resources, COLORS_SCROLL_TRACK, COLORS_SCROLL_THUMB));
        launchSelect.setFocusable(false);

        child = launchSelect.getAccessibleContext().getAccessibleChild(0);
        popup = (BasicComboPopup)child;
        list = popup.getList();
        list.setSelectionForeground(COLORS_BUTTON_BLUE);
        list.setSelectionBackground(COLORS_FORMELEMENT_INTERNAL);
        list.setBackground(COLORS_CENTRAL_BACK_OPAQUE);

        panel.add(launchSelect, new GridBagConstraints(1, 2, 2, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(8, 16, 8, 16), 0, 16));

        //Install folder field
        JLabel installLabel = new JLabel(resources.getString("launcheroptions.general.install"));
        installLabel.setFont(BitcraftPanel.mainFont.deriveFont(Font.PLAIN, 16));
        installLabel.setForeground(COLORS_WHITE_TEXT);
        panel.add(installLabel, new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 40, 0, 0), 0, 0));

        installField = new JTextField("");
        installField.setFont(BitcraftPanel.mainFont.deriveFont(Font.PLAIN, 16));
        installField.setForeground(COLORS_BLUE);
        installField.setBackground(COLORS_FORMELEMENT_INTERNAL);
        installField.setHighlighter(null);
        installField.setEditable(false);
        installField.setCursor(null);
        installField.setBorder(new RoundBorder(COLORS_BUTTON_BLUE, 1, 8));
        panel.add(installField, new GridBagConstraints(1, 3, 2, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(8, 16, 8, 16), 0, 16));

        RoundedButton reinstallButton = new RoundedButton(resources.getString("launcheroptions.install.change"));
        reinstallButton.setFont(BitcraftPanel.mainFont.deriveFont(Font.PLAIN, 16));
        reinstallButton.setContentAreaFilled(false);
        reinstallButton.setForeground(COLORS_BUTTON_BLUE);
        reinstallButton.setHoverForeground(COLORS_BLUE);
        reinstallButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reinstall();
            }
        });
        panel.add(reinstallButton, new GridBagConstraints(3, 3, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(8, 0, 8, 0), 0, 0));

        //Client ID field
        JLabel clientIdField = new JLabel(resources.getString("launcheroptions.general.id"));
        clientIdField.setFont(BitcraftPanel.mainFont.deriveFont(Font.PLAIN, 16));
        clientIdField.setForeground(COLORS_WHITE_TEXT);
        panel.add(clientIdField, new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 40, 0, 0), 0, 0));

        clientId = new JTextField("abc123");
        clientId.setFont(BitcraftPanel.mainFont.deriveFont(Font.PLAIN, 16));
        clientId.setForeground(COLORS_BLUE);
        clientId.setBackground(COLORS_FORMELEMENT_INTERNAL);
        clientId.setHighlighter(null);
        clientId.setEditable(false);
        clientId.setCursor(null);
        clientId.setBorder(new RoundBorder(COLORS_BUTTON_BLUE, 1, 8));
        panel.add(clientId, new GridBagConstraints(1, 4, 2, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(8, 16, 8, 16), 0, 16));

        RoundedButton copyButton = new RoundedButton(resources.getString("launcheroptions.id.copy"));
        copyButton.setFont(BitcraftPanel.mainFont.deriveFont(Font.PLAIN, 16));
        copyButton.setContentAreaFilled(false);
        copyButton.setForeground(COLORS_BUTTON_BLUE);
        copyButton.setHoverForeground(COLORS_BLUE);
        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyCid();
            }
        });
        panel.add(copyButton, new GridBagConstraints(3, 4, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(8, 0, 8, 0), 0, 0));

        panel.add(Box.createRigidArea(new Dimension(60, 0)), new GridBagConstraints(4, 3, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0));

        //Add show console field
        JLabel showConsoleField = new JLabel(resources.getString("launcheroptions.general.console"));
        showConsoleField.setFont(BitcraftPanel.mainFont.deriveFont(Font.PLAIN, 16));
        showConsoleField.setForeground(COLORS_WHITE_TEXT);
        panel.add(showConsoleField, new GridBagConstraints(0, 5, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 40, 0, 0), 0, 0));

        showConsole = new JCheckBox("", false);
        showConsole.setOpaque(false);
        showConsole.setHorizontalAlignment(SwingConstants.RIGHT);
        showConsole.setBorder(BorderFactory.createEmptyBorder());
        showConsole.setIconTextGap(0);
        showConsole.setSelectedIcon(resources.getIcon("checkbox_closed.png"));
        showConsole.setIcon(resources.getIcon("checkbox_open.png"));
        showConsole.setFocusPainted(false);

        panel.add(showConsole, new GridBagConstraints(1, 5, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(16, 16, 0, 0), 0, 0));

        //Add launch to modpacks
        
        JLabel launchToModpacksField = new JLabel(resources.getString("launcheroptions.general.modpacktab"));
        launchToModpacksField.setFont(BitcraftPanel.mainFont.deriveFont(Font.PLAIN, 16));
        launchToModpacksField.setForeground(COLORS_WHITE_TEXT);
        panel.add(launchToModpacksField, new GridBagConstraints(0,6,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(10,40,0,0),0,0));

        launchToModpacks = new JCheckBox("", false);
        launchToModpacks.setOpaque(false);
        launchToModpacks.setHorizontalAlignment(SwingConstants.RIGHT);
        launchToModpacks.setBorder(BorderFactory.createEmptyBorder());
        launchToModpacks.setIconTextGap(0);
        launchToModpacks.setSelectedIcon(resources.getIcon("checkbox_closed.png"));
        launchToModpacks.setIcon(resources.getIcon("checkbox_open.png"));
        launchToModpacks.setFocusPainted(false);

        panel.add(launchToModpacks, new GridBagConstraints(1, 6, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(16, 16 ,0, 0), 0,0));

        panel.add(Box.createGlue(), new GridBagConstraints(0, 7, 5, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));

        //Open logs button
        RoundedButton openLogs = new RoundedButton(resources.getString("launcheroptions.general.logs"));
        openLogs.setFont(BitcraftPanel.mainFont.deriveFont(Font.PLAIN, 16));
        openLogs.setContentAreaFilled(false);
        openLogs.setForeground(COLORS_BUTTON_BLUE);
        openLogs.setHoverForeground(COLORS_BLUE);
        openLogs.setBorder(BorderFactory.createEmptyBorder(5, 17, 10, 17));
        openLogs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openLogs();
            }
        });
        panel.add(openLogs, new GridBagConstraints(0, 8, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 10, 0), 0, 0));
    }

    private void updateDimensionsEnabled() {
        if (windowSelect.getSelectedIndex() == 2) {
            widthInput.setEnabled(true);
            heightInput.setEnabled(true);
            widthInput.setForeground(COLORS_BUTTON_BLUE);
            heightInput.setForeground(COLORS_BUTTON_BLUE);
            widthInput.setBorder(new RoundBorder(COLORS_BUTTON_BLUE, 1, 8));
            heightInput.setBorder(new RoundBorder(COLORS_BUTTON_BLUE, 1, 8));
        } else {
            widthInput.setEnabled(false);
            heightInput.setEnabled(false);
            widthInput.setForeground(COLORS_GREY_TEXT);
            heightInput.setForeground(COLORS_GREY_TEXT);
            widthInput.setBorder(new RoundBorder(COLORS_GREY_TEXT, 1, 8));
            heightInput.setBorder(new RoundBorder(COLORS_GREY_TEXT, 1, 8));
        }
    }

    private void setupVideoOptionsPanel(JPanel panel) {
        panel.setLayout(new GridBagLayout());

        JLabel streamLabel = new JLabel(resources.getString("launcheroptions.video.windowSize"));
        streamLabel.setFont(BitcraftPanel.mainFont.deriveFont(Font.PLAIN, 16));
        streamLabel.setForeground(COLORS_WHITE_TEXT);
        panel.add(streamLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 40, 0, 0), 0, 0));

        windowSelect = new JComboBox();

        if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("mac")) {
            windowSelect.setUI(new MetalComboBoxUI());
        }

        windowSelect.setFont(BitcraftPanel.mainFont.deriveFont(Font.PLAIN, 16));
        windowSelect.setEditable(false);
        windowSelect.setBorder(new RoundBorder(COLORS_BUTTON_BLUE, 1, 10));
        windowSelect.setForeground(COLORS_BUTTON_BLUE);
        windowSelect.setBackground(COLORS_FORMELEMENT_INTERNAL);
        windowSelect.setUI(new SimpleButtonComboUI(new RoundedBorderFormatter(new RoundBorder(COLORS_BUTTON_BLUE, 1, 4)), resources, COLORS_SCROLL_TRACK, COLORS_SCROLL_THUMB));
        windowSelect.setFocusable(false);

        Object child = windowSelect.getAccessibleContext().getAccessibleChild(0);
        BasicComboPopup popup = (BasicComboPopup)child;
        JList list = popup.getList();
        list.setSelectionForeground(COLORS_BUTTON_BLUE);
        list.setSelectionBackground(COLORS_FORMELEMENT_INTERNAL);
        list.setBackground(COLORS_CENTRAL_BACK_OPAQUE);

        panel.add(windowSelect, new GridBagConstraints(1, 0, 1, 1, 0.5f, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(8, 16, 8, 16), 0, 16));

        JLabel widthLabel = new JLabel(resources.getString("launcheroptions.video.windowSize.width"));
        widthLabel.setFont(BitcraftPanel.mainFont.deriveFont(Font.PLAIN, 16));
        widthLabel.setForeground(COLORS_WHITE_TEXT);
        panel.add(widthLabel, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        widthInput = new JTextField(3);
        widthInput.setFont(BitcraftPanel.mainFont.deriveFont(Font.PLAIN, 16));
        widthInput.setForeground(new Color(68,68,68));
        widthInput.setBackground(COLORS_FORMELEMENT_INTERNAL);
        widthInput.setBorder(new RoundBorder(COLORS_BUTTON_BLUE, 1, 8));
        widthInput.setCaretColor(COLORS_BLUE);
        widthInput.setText("800");
        panel.add(widthInput, new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(8, 6, 8, 16), 0, 0));

        JLabel heightLabel = new JLabel(resources.getString("launcheroptions.video.windowSize.height"));
        heightLabel.setFont(BitcraftPanel.mainFont.deriveFont(Font.PLAIN, 16));
        heightLabel.setForeground(COLORS_WHITE_TEXT);
        panel.add(heightLabel, new GridBagConstraints(4, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        heightInput = new JTextField(3);
        heightInput.setFont(BitcraftPanel.mainFont.deriveFont(Font.PLAIN, 16));
        heightInput.setForeground(new Color(68,68,68));
        heightInput.setBackground(COLORS_FORMELEMENT_INTERNAL);
        heightInput.setBorder(new RoundBorder(COLORS_BUTTON_BLUE, 1, 8));
        heightInput.setCaretColor(COLORS_BLUE);
        heightInput.setText("600");
        panel.add(heightInput, new GridBagConstraints(5, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(8, 6, 8, 16), 0,0));

        //Add show console field
        JLabel useStencilField = new JLabel(resources.getString("launcheroptions.video.stencil"));
        useStencilField.setFont(BitcraftPanel.mainFont.deriveFont(Font.PLAIN, 16));
        useStencilField.setForeground(COLORS_WHITE_TEXT);
        panel.add(useStencilField, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 40, 0, 0), 0, 0));

        useStencil = new JComboBox();

        if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("mac")) {
            useStencil.setUI(new MetalComboBoxUI());
        }

        useStencil.setFont(BitcraftPanel.mainFont.deriveFont(Font.PLAIN, 16));
        useStencil.setEditable(false);
        useStencil.setBorder(new RoundBorder(COLORS_BUTTON_BLUE, 1, 10));
        useStencil.setForeground(COLORS_BUTTON_BLUE);
        useStencil.setBackground(COLORS_FORMELEMENT_INTERNAL);
        useStencil.setUI(new SimpleButtonComboUI(new RoundedBorderFormatter(new RoundBorder(COLORS_BUTTON_BLUE, 1, 4)), resources, COLORS_SCROLL_TRACK, COLORS_SCROLL_THUMB));
        useStencil.setFocusable(false);

        child = useStencil.getAccessibleContext().getAccessibleChild(0);
        popup = (BasicComboPopup)child;
        list = popup.getList();
        list.setSelectionForeground(COLORS_BUTTON_BLUE);
        list.setSelectionBackground(COLORS_FORMELEMENT_INTERNAL);
        list.setBackground(COLORS_CENTRAL_BACK_OPAQUE);
        panel.add(useStencil, new GridBagConstraints(1, 1, 1, 1, 0.5f, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(8, 16, 8, 16), 0, 16));

        JLabel stencilInfo = new JLabel("") {
            @Override
            public Dimension getMaximumSize() {
                return getMinimumSize();
            }

            @Override
            public Dimension getPreferredSize() {
                return getMinimumSize();
            }
        };
        stencilInfo.setFont(BitcraftPanel.mainFont.deriveFont(Font.PLAIN, 12));
        stencilInfo.setForeground(COLORS_WHITE_TEXT);

        stencilInfo.setText("<html><body style=\"font-family:" + stencilInfo.getFont().getFamily() + ";color:#444444\">" + resources.getString("launcheroptions.video.stencil.info") + "</body></html>");

        panel.add(stencilInfo, new GridBagConstraints(2, 1, 4, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        panel.add(Box.createHorizontalStrut(60), new GridBagConstraints(7, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0,0,0,0), 30,0));

        panel.add(Box.createGlue(), new GridBagConstraints(0, 2, 8, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0));
    }

    private void setupJavaOptionsPanel(JPanel panel) {
        panel.setLayout(new GridBagLayout());

        JLabel versionLabel = new JLabel(resources.getString("launcheroptions.java.version"));
        versionLabel.setFont(BitcraftPanel.mainFont.deriveFont(Font.PLAIN, 16));
        versionLabel.setForeground(COLORS_WHITE_TEXT);
        panel.add(versionLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 60, 0, 0), 0, 0));

        versionSelect = new JComboBox();

        if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("mac")) {
            versionSelect.setUI(new MetalComboBoxUI());
        }

        versionSelect.setFont(BitcraftPanel.mainFont.deriveFont(Font.PLAIN, 16));
        versionSelect.setEditable(false);
        versionSelect.setBorder(new RoundBorder(COLORS_BUTTON_BLUE, 1, 10));
        versionSelect.setForeground(COLORS_BUTTON_BLUE);
        versionSelect.setBackground(COLORS_FORMELEMENT_INTERNAL);
        SimpleButtonComboUI ui = new SimpleButtonComboUI(new RoundedBorderFormatter(new RoundBorder(COLORS_BUTTON_BLUE, 1, 4)), resources, COLORS_SCROLL_TRACK, COLORS_SCROLL_THUMB);
        versionSelect.setUI(ui);
        versionSelect.setFocusable(false);

        Object child = versionSelect.getAccessibleContext().getAccessibleChild(0);
        BasicComboPopup popup = (BasicComboPopup)child;
        JList list = popup.getList();
        list.setSelectionForeground(COLORS_BUTTON_BLUE);
        list.setSelectionBackground(COLORS_FORMELEMENT_INTERNAL);
        list.setBackground(COLORS_CENTRAL_BACK_OPAQUE);

        panel.add(versionSelect, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(8, 16, 8, 8), 0, 16));

        RoundedButton otherVersionButton = new RoundedButton(resources.getString("launcheroptions.java.otherversion"));
        otherVersionButton.setFont(BitcraftPanel.mainFont.deriveFont(Font.PLAIN, 16));
        otherVersionButton.setContentAreaFilled(false);
        otherVersionButton.setForeground(COLORS_BUTTON_BLUE);
        otherVersionButton.setHoverForeground(COLORS_BLUE);
        otherVersionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectOtherVersion();
            }
        });
        panel.add(otherVersionButton, new GridBagConstraints(2, 0, 5, 1, 2, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(8, 2, 2, 80), 0, 0));

        JLabel memLabel = new JLabel(resources.getString("launcheroptions.java.memory"));
        memLabel.setFont(BitcraftPanel.mainFont.deriveFont(Font.PLAIN, 16));
        memLabel.setForeground(COLORS_WHITE_TEXT);
        panel.add(memLabel, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 60, 0, 0), 0, 0));

        memSelect = new JComboBox();

        if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("mac")) {
            memSelect.setUI(new MetalComboBoxUI());
        }

        memSelect.setFont(BitcraftPanel.mainFont.deriveFont(Font.PLAIN, 16));
        memSelect.setEditable(false);
        memSelect.setBorder(new RoundBorder(COLORS_BUTTON_BLUE, 1, 10));
        memSelect.setForeground(COLORS_BUTTON_BLUE);
        memSelect.setBackground(COLORS_FORMELEMENT_INTERNAL);
         ui = new SimpleButtonComboUI(new RoundedBorderFormatter(new RoundBorder(COLORS_BUTTON_BLUE, 1, 4)), resources, COLORS_SCROLL_TRACK, COLORS_SCROLL_THUMB);
        memSelect.setUI(ui);
        memSelect.setFocusable(false);

        child = memSelect.getAccessibleContext().getAccessibleChild(0);
        popup = (BasicComboPopup)child;
        list = popup.getList();
        list.setSelectionForeground(COLORS_BUTTON_BLUE);
        list.setSelectionBackground(COLORS_FORMELEMENT_INTERNAL);
        list.setBackground(COLORS_CENTRAL_BACK_OPAQUE);
        panel.add(memSelect, new GridBagConstraints(1, 1, 6, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(8, 16, 8, 80), 0, 16));

        JLabel argsLabel = new JLabel(resources.getString("launcheroptions.java.arguments"));
        argsLabel.setFont(BitcraftPanel.mainFont.deriveFont(Font.PLAIN, 16));
        argsLabel.setForeground(COLORS_WHITE_TEXT);
        panel.add(argsLabel, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 60, 0, 0), 0, 0));

        javaArgs = new JTextArea(32, 4);
        javaArgs.setFont(BitcraftPanel.mainFont.deriveFont(Font.PLAIN, 16));
        javaArgs.setForeground(COLORS_BUTTON_BLUE);
        javaArgs.setBackground(COLORS_FORMELEMENT_INTERNAL);
        javaArgs.setBorder(new RoundBorder(COLORS_BUTTON_BLUE, 1, 8));
        javaArgs.setCaretColor(COLORS_BUTTON_BLUE);
        javaArgs.setMargin(new Insets(16, 4, 16, 4));
        javaArgs.setLineWrap(true);
        javaArgs.setWrapStyleWord(true);
        javaArgs.setSelectionColor(COLORS_BUTTON_BLUE);
        javaArgs.setSelectedTextColor(COLORS_FORMELEMENT_INTERNAL);

        panel.add(javaArgs, new GridBagConstraints(1, 2, 6, 2, 0, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(8, 16, 6, 80), 0, 0));

        JLabel autoApprovalLabel = new JLabel(resources.getString("launcheroptions.java.autoApprove"));
        autoApprovalLabel.setFont(BitcraftPanel.mainFont.deriveFont(Font.PLAIN, 16));
        autoApprovalLabel.setForeground(COLORS_WHITE_TEXT);
        panel.add(autoApprovalLabel, new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));

        askFirstBox = new JCheckBox("", false);
        askFirstBox.setOpaque(false);
        askFirstBox.setHorizontalAlignment(SwingConstants.RIGHT);
        askFirstBox.setBorder(BorderFactory.createEmptyBorder());
        askFirstBox.setIconTextGap(0);
        askFirstBox.setSelectedIcon(resources.getIcon("checkbox_closed.png"));
        askFirstBox.setIcon(resources.getIcon("checkbox_open.png"));
        askFirstBox.setFocusPainted(false);
        panel.add(askFirstBox, new GridBagConstraints(1, 4, 6, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(8, 16, 8, 8), 0, 0));

        panel.add(Box.createGlue(), new GridBagConstraints(4, 5, 1, 1, 1, 0.5, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0));
    }

    @Override
    public void relocalize(ResourceLoader loader) {
        this.resources = loader;
        this.resources.registerResource(this);

        //Wipe controls
        this.getContentPane().removeAll();
        this.setLayout(null);

        initComponents();
        initControlValues();

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                invalidate();
                repaint();
            }
        });
    }
}
