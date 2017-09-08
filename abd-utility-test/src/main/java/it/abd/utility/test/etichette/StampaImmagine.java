package it.abd.utility.test.etichette;
/***********************************************
 * CONFIDENTIAL AND PROPRIETARY 
 * 
 * The source code and other information contained herein is the confidential and the exclusive property of
 * ZIH Corp. and is subject to the terms and conditions in your end user license agreement.
 * This source code, and any other information contained herein, shall not be copied, reproduced, published, 
 * displayed or distributed, in whole or in part, in any medium, by any means, for any purpose except as
 * expressly permitted under such license agreement.
 * 
 * Copyright ZIH Corp. 2012
 * 
 * ALL RIGHTS RESERVED
 ***********************************************/


import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

import javax.imageio.ImageIO;

import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionBuilder;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

/**
 *
 *
 */
public class StampaImmagine {
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");

    public static String convertImage(InputStream imageInputStream, int xPos, int yPos) throws IOException {

        // BufferedImage img = ImageIO.read(new File(filename));
        BufferedImage img = ImageIO.read(imageInputStream);

        int height = img.getHeight();
        int width = img.getWidth();

        int P3 = (int) Math.ceil((double) width / 8);

        StringBuffer sb = new StringBuffer("");
        sb.append("GW" + xPos + "," + yPos + "," + P3 + "," + height + ",");

        int canvasWidth = P3 * 8;

        for (int y = 0; y < height; y++) // loop from top to bottom
        {
            for (int x = 0; x < canvasWidth;) // from left to right
            {
                byte abyte = 0;

                for (int b = 0; b < 8; b++, x++) // get 8 bits together and write to memory
                {

                    int dot = -128; // set 0 for white,1 for black
                    // pixel still in width of bitmap,
                    // check luminance for white or black, out of bitmap set to white
                    if (x < width) {
                        ;

                        int rgb = img.getRGB(x, y);
                        // sb.append(rgb);
                        int red = (rgb >> 16) & 0x000000FF;
                        int green = (rgb >> 8) & 0x000000FF;
                        int blue = (rgb) & 0x000000FF;

                        int luminance = (int) ((red * 0.3) + (green * 0.59) + (blue * 0.11));
                        dot = luminance > 127 ? 1 : 0;

                    }
                    abyte |= (byte) (dot << (7 - b)); // shift left,
                    // then OR together to get 8 bits into a byte
                }
                sb.append(new String(new byte[] { abyte }, "Windows-1252"));
            }
        }

        return sb.toString();
    }

    public static void main(String[] args) throws ConnectionException, ZebraPrinterLanguageUnknownException, IOException {
        Connection connection = ConnectionBuilder.build("TCP:192.168.1.79");
        System.out.println("Connection string evaluated as class type " + connection.getClass().getSimpleName());
        connection.open();

        ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);
        System.out.println(printer.getPrinterControlLanguage().name());
        // printer.sendCommand("^XA^FO100,50^ADN,36,20^FDxxx^FS^XZ");
        StringBuffer asd = new StringBuffer("N\n");

        // asd.append("b10,20,D,c26,r26,h6,\"12df23db-1asd-asda-156dfbvd8w1f\"\n");
        InputStream is = new FileInputStream("C:\\Users\\Pancio\\Downloads\\logo notartel ok.bmp");
        asd.append(convertImage(is, 10, 25));

        /*
         * EPL TEXT PRINT
         * 
         * Ap1,p2,p3,p4,p5,p6,p7,�DATA�
         * 
         * p1 = Horizontal start position (X) in dots
         * p2 = Vertical start position (Y) in dots
         * p3 = Rotation Characters are organized vertically from left to right and then rotated to print.
         *      Accepted Values:
         *      0 = normal (no rotation)
         *      1 = 90 degrees
         *      2 = 180 degrees
         *      3 = 270 degrees
         *      
         * p4 = Font selection    
         * p5 = Horizontal multiplier
         * p6 = Vertical multiplier
         * p7 = Reverse image 
         * DATA = Fixed data field
         * 
         */

        asd.append("A40,0,0,2,1,1,N,\"Consiglio Nazionale Notariato\"\n");
        // asd.append("A180,35,0,3,1,1,N,\"Ufficio\"\n");
        // asd.append("A180,60,0,2,1,1,N,\"Gestione del\"\n");
        // asd.append("A180,80,0,2,1,1,N,\"Personale\"\n");
        // asd.append("A180,60,0,2,1,1,N,\"del Notariato\"\n");
        asd.append("A180,35,0,4,1,2,N,\"Protocollo\"\n");
        asd.append("A180,90,0,4,1,2,N,\"0000002/2016\"\n");
        asd.append("A180,140,0,4,1,2,N,\"31/03/2016\"\n");

        asd.append("P1\n");
        printer.sendCommand(asd.toString());
        // Thread.sleep(500);
        connection.close();
    }
}
