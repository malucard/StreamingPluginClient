/*******************************************************************************
 * Copyright (c) 2018 Maschell
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/

package de.mas.wiiu.streaming.gui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;
import java.time.OffsetDateTime;

import com.jagrosh.discordipc.*;
import com.jagrosh.discordipc.entities.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.WindowConstants;

public class StreamWindow {
    private final Dimension screenSize = new Dimension(854, 480);
    private final ImagePanel image = new ImagePanel(screenSize.width, screenSize.height);

    public StreamWindow(IImageProvider imageProvider) {
        OffsetDateTime start = OffsetDateTime.now();
        IPCClient client = new IPCClient(793963977277046824L);
        client.setListener(new IPCListener() {
            @Override
            public void onReady(IPCClient client)  {
                RichPresence.Builder builder = new RichPresence.Builder();
                builder.setState("Unknown").setStartTimestamp(start);
                client.sendRichPresence(builder.build());
            }
        });
        try {
            client.connect();
        } catch(Exception e) {
            e.printStackTrace();
        }

        JFrame editorFrame = new JFrame("Wii U");
        JFrame editorFrame2 = new JFrame("wiiu");

	    editorFrame.setResizable(false);
        editorFrame.setMaximumSize(screenSize);
        editorFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        imageProvider.setOnImageChange((bi, size) -> {
            int w = size >> 16;
            int h = size & 0xFFFF;
            image.setImage(bi, w, h);
            editorFrame.setSize(w, h + 36);
        });
        editorFrame.getContentPane().add(image);

        JMenuBar menuBar = new JMenuBar();
        editorFrame2.getContentPane().add(menuBar, BorderLayout.NORTH);

        JMenu mnSettings = new JMenu("Settings");
        menuBar.add(mnSettings);


        JMenuItem mntmNewMenuItem = new JMenuItem("Set Title");
        mntmNewMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { //Create a JFrame to show the icon in the taskbar
                final JFrame frame = new JFrame("Wii U Streaming Client - Enter Title...");
                frame.setUndecorated( true );
                frame.setVisible( true );
                frame.setLocationRelativeTo( null );

                //Display the IP Dialog
                String title = JOptionPane.showInputDialog(frame, "Please enter the window title", "Wii U streaming client", JOptionPane.PLAIN_MESSAGE);

                //Check if user clicked "Cancel"
                if(title != null) {
                    editorFrame.setTitle(title);
                    RichPresence.Builder builder = new RichPresence.Builder();
                    builder.setState(title).setStartTimestamp(start);
                    client.sendRichPresence(builder.build());
                }
                //Close the JFrame again
                frame.dispose();
            }
        });
        JMenuItem mntmExit = new JMenuItem("Exit");
        mntmExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        //mntmNewMenuItem.setEnabled(false);
        mnSettings.add(mntmNewMenuItem);
        mnSettings.add(mntmExit);

        editorFrame.pack();
        editorFrame.setLocationRelativeTo(null);
        editorFrame.setVisible(true);
        editorFrame2.pack();
        editorFrame2.setLocationRelativeTo(null);
        editorFrame2.setVisible(true);
    }

}
