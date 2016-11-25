
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;
import java.lang.*;


class holder {
	double r;
	double g;
	double b;

	public holder(double r, double g, double b){
		this.r = r;
		this.g = g;
		this.b = b;
	}
}


public class imageReader {

	JFrame frame;
	JLabel lbIm1;
	JLabel lbIm2;
	BufferedImage img;
	BufferedImage myimg;

	double checkbound(double input, double uper, double lower){
		if(input < lower)
			return lower;
		if(input > uper)
			return uper;

		return input;
	}

	public void showIms(String[] args){
		int width = 512;
		int height = 512;
		int coinput = Integer.parseInt(args[1]);
		int dwtco = coinput;
		int dctco = (int)Math.round(coinput * 1.000/4096.000);
		int recursion = 64;

		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		myimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);


		// Use labels to display the images
		frame = new JFrame();
		GridBagLayout gLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gLayout);

		JLabel lbText1 = new JLabel("DCT (Left)");
		lbText1.setHorizontalAlignment(SwingConstants.CENTER);
		JLabel lbText2 = new JLabel("DWT (Right)");
		lbText2.setHorizontalAlignment(SwingConstants.CENTER);
		lbIm1 = new JLabel(new ImageIcon(img));
		lbIm2 = new JLabel(new ImageIcon(myimg));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;
		frame.getContentPane().add(lbText1, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 0;
		frame.getContentPane().add(lbText2, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		frame.getContentPane().add(lbIm1, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 1;
		frame.getContentPane().add(lbIm2, c);

		frame.pack();
		frame.setVisible(true);

		if(coinput == -1)
			recursion = 1;

		while(true){

			if(coinput == -1){
				dwtco = recursion * 4096;
				dctco = recursion;
			}

		holder[][] dctmatrix = new holder[height][width];
		holder[][] dwtmatrix = new holder[height][width];

		holder[][] dctmatrixprocess = new holder[height][width];
		holder[][] dwtmatrixprocess = new holder[height][width];
		holder[][] dwtmatrixtemp = new holder[height][width];

		try {
			File file = new File(args[0]);
			InputStream is = new FileInputStream(file);

			long len = file.length();
			byte[] bytes = new byte[(int)len];

			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
				offset += numRead;
			}

			//reading file
			int ind = 0;
			for(int y = 0; y < height; y++){

				for(int x = 0; x < width; x++){

					byte a = 0;
					byte r = bytes[ind];
					byte g = bytes[ind+height*width];
					byte b = bytes[ind+height*width*2]; 

					short sr = (short)(r & 0xff);
					short gr = (short)(g & 0xff);
					short br = (short)(b & 0xff);

					int rr = sr;
					int gg = gr ;
					int bb = br;



					dctmatrix[y][x] = new holder(rr/1.000, gg/1.000, bb/1.000);
					dwtmatrix[y][x] = new holder(rr/1.000, gg/1.000, bb/1.000);
					dwtmatrixprocess[y][x] = new holder(rr/1.000, gg/1.000, bb/1.000);
					dwtmatrixtemp[y][x] = new holder(rr/1.000, gg/1.000, bb/1.000);

					
					ind++;
				}
			}

			//DCT encoding
			for(int y = 0; y < height; y += 8){
				for(int x = 0; x < width; x += 8){
					for(int yind = 0; yind < 8; yind ++){
						for(int xind = 0; xind < 8; xind ++){
							
							double tempr = 0.000;
							double tempg = 0.000;
							double tempb = 0.000;
							for(int yinner = y; yinner < y + 8; yinner ++){
								for(int xinner = x; xinner < x + 8; xinner ++){
									tempr += dctmatrix[yinner][xinner].r * Math.cos((2*(xinner-x) + 1) * xind * Math.PI / 16.000) * Math.cos((2*(yinner-y) + 1) * yind * Math.PI / 16.000);
									tempg += dctmatrix[yinner][xinner].g * Math.cos((2*(xinner-x) + 1) * xind * Math.PI / 16.000) * Math.cos((2*(yinner-y) + 1) * yind * Math.PI / 16.000);
									tempb += dctmatrix[yinner][xinner].b * Math.cos((2*(xinner-x) + 1) * xind * Math.PI / 16.000) * Math.cos((2*(yinner-y) + 1) * yind * Math.PI / 16.000);

								}
							}

							double cx = 1.000;
							double cy = 1.000;
							if(yind == 0)
								cy = 1.000/ Math.sqrt(2);
							if(xind == 0)
								cx = 1.000/ Math.sqrt(2);

							//System.out.println(((1/4.000) * cy * cx * tempr) + " " + ((1/4.000) * cy * cx * tempg) + " " + ((1/4.000) * cy * cx * tempb));

							dctmatrixprocess[y+yind][x+xind] = new holder(((1/4.000) * cy * cx * tempr), ((1/4.000) * cy * cx * tempg), ((1/4.000) * cy * cx * tempb));

						}
					}
				}
			}

			//Dct set coefficient
			for(int y = 0; y < height; y += 8){
				for(int x = 0; x < width; x += 8){
					int dcty = 1;
					int dctx = 1;
					for(int i = 0; i < 64; i++){
						if(i >= dctco){
							dctmatrixprocess[dcty+y-1][dctx+x-1].r = 0.000;
							dctmatrixprocess[dcty+y-1][dctx+x-1].g = 0.000;
							dctmatrixprocess[dcty+y-1][dctx+x-1].b = 0.000;
						}
						if((dctx + dcty) % 2 == 0){
							if(dctx < 8)
								dctx ++;
							else
								dcty += 2;

							if(dcty > 1)
								dcty --;
						}
						else{
							if(dcty < 8)
								dcty ++;
							else
								dctx += 2;

							if(dctx > 1)
								dctx --;
						}
					}	
				}
			}


			//Dct decoding
			for(int y = 0; y < height; y += 8){
				for(int x = 0; x < width; x += 8){
					for(int yind = 0; yind < 8; yind ++){
						for(int xind = 0; xind < 8; xind ++){

							double tempr = 0.000;
							double tempg = 0.000;
							double tempb = 0.000;
							for(int yinner = y; yinner < y + 8; yinner ++){
								for(int xinner = x; xinner < x + 8; xinner ++){

									double cx = 1.000;
									double cy = 1.000;
									if(yinner == y)
										cy = 1.000/ Math.sqrt(2);
									if(xinner == x)
										cx = 1.000/ Math.sqrt(2);

									tempr += dctmatrixprocess[yinner][xinner].r * cx * cy * Math.cos((2* xind + 1) * (xinner - x) * Math.PI / 16.000) * Math.cos((2*yind + 1) * (yinner - y) * Math.PI / 16.000);
									tempg += dctmatrixprocess[yinner][xinner].g * cx * cy * Math.cos((2* xind + 1) * (xinner - x) * Math.PI / 16.000) * Math.cos((2*yind + 1) * (yinner - y) * Math.PI / 16.000);
									tempb += dctmatrixprocess[yinner][xinner].b * cx * cy * Math.cos((2* xind + 1) * (xinner - x) * Math.PI / 16.000) * Math.cos((2*yind + 1) * (yinner - y) * Math.PI / 16.000);

								}
							}

							dctmatrix[y+yind][x+xind].r = checkbound((1.000/4) * tempr, 255.000, 0.000);
							dctmatrix[y+yind][x+xind].g = checkbound((1.000/4) * tempg, 255.000, 0.000);
							dctmatrix[y+yind][x+xind].b = checkbound((1.000/4) * tempb, 255.000, 0.000);
						}
					}
				}
			}


			//DWT encoding  (left plus, right minus)
			int dwth = height;
			int dwtw = width;

			while(dwtw != 1){

				for(int y = 0; y < dwth; y ++ ){
					for(int x = 0; x < dwtw; x += 2){
						int first = x / 2;
						int second = first + dwtw/2;
						dwtmatrixtemp[y][first].r = (dwtmatrixprocess[y][x].r + dwtmatrixprocess[y][x+1].r) / 2.000;
						dwtmatrixtemp[y][second].r = (dwtmatrixprocess[y][x].r - dwtmatrixprocess[y][x+1].r) / 2.000;
						dwtmatrixtemp[y][first].g = (dwtmatrixprocess[y][x].g + dwtmatrixprocess[y][x+1].g) / 2.000;
						dwtmatrixtemp[y][second].g = (dwtmatrixprocess[y][x].g - dwtmatrixprocess[y][x+1].g) / 2.000;
						dwtmatrixtemp[y][first].b = (dwtmatrixprocess[y][x].b + dwtmatrixprocess[y][x+1].b) / 2.000;
						dwtmatrixtemp[y][second].b = (dwtmatrixprocess[y][x].b - dwtmatrixprocess[y][x+1].b) / 2.000;
						//System.out.println(dwtmatrixtemp[y][second].r + " " + dwtmatrixtemp[y][second].g + " "+dwtmatrixtemp[y][second].b);
					}
				}

				for(int x = 0; x < dwtw; x ++ ){
					for(int y = 0; y < dwth; y += 2){
						int first = y / 2;
						int second = first + dwth/2;
						dwtmatrixprocess[first][x].r = (dwtmatrixtemp[y][x].r + dwtmatrixtemp[y+1][x].r) / 2.000;
						dwtmatrixprocess[second][x].r = (dwtmatrixtemp[y][x].r - dwtmatrixtemp[y+1][x].r) / 2.000;
						dwtmatrixprocess[first][x].g = (dwtmatrixtemp[y][x].g + dwtmatrixtemp[y+1][x].g) / 2.000;
						dwtmatrixprocess[second][x].g = (dwtmatrixtemp[y][x].g - dwtmatrixtemp[y+1][x].g) / 2.000;
						dwtmatrixprocess[first][x].b = (dwtmatrixtemp[y][x].b + dwtmatrixtemp[y+1][x].b) / 2.000;
						dwtmatrixprocess[second][x].b = (dwtmatrixtemp[y][x].b - dwtmatrixtemp[y+1][x].b) / 2.000;	
						//System.out.println(dwtmatrixtemp[second][x].r + " " + dwtmatrixtemp[second][x].g + " "+dwtmatrixtemp[second][x].b);					
					}
				}

				dwth /= 2;
				dwtw /= 2;
			}


			//DWT set coefficient
			System.out.println(recursion);
			int dwty = 1;
			int dwtx = 1;
			for(int i = 0; i < height * width; i++){
				if(i >= dwtco){
					dwtmatrixprocess[dwty-1][dwtx-1].r = 0.000;
					dwtmatrixprocess[dwty-1][dwtx-1].g = 0.000;
					dwtmatrixprocess[dwty-1][dwtx-1].b = 0.000;
				}
				if((dwtx + dwty) % 2 == 0){
					if(dwtx < width)
						dwtx ++;
					else
						dwty += 2;

					if(dwty > 1)
						dwty --;
				}
				else{
					if(dwty < height)
						dwty ++;
					else
						dwtx += 2;

					if(dwtx > 1)
						dwtx --;
				}
			}

			//DWT decoding

			dwth = 2;
			dwtw = 2;

			while(! (dwtw > width) ){

				for(int x = 0; x < dwtw; x ++ ){
					for(int y = 0; y < dwth; y += 2){
						int first = y / 2;
						int second = first + dwth/2;
						dwtmatrixtemp[y][x].r = dwtmatrixprocess[first][x].r + dwtmatrixprocess[second][x].r;
						dwtmatrixtemp[y+1][x].r = dwtmatrixprocess[first][x].r - dwtmatrixprocess[second][x].r;
						dwtmatrixtemp[y][x].g = dwtmatrixprocess[first][x].g + dwtmatrixprocess[second][x].g;
						dwtmatrixtemp[y+1][x].g = dwtmatrixprocess[first][x].g - dwtmatrixprocess[second][x].g;
						dwtmatrixtemp[y][x].b = dwtmatrixprocess[first][x].b + dwtmatrixprocess[second][x].b;
						dwtmatrixtemp[y+1][x].b = dwtmatrixprocess[first][x].b - dwtmatrixprocess[second][x].b;


					}
				}

				for(int y = 0; y < dwth; y ++ ){
					for(int x = 0; x < dwtw; x += 2){
						int first = x / 2;
						int second = first + dwtw/2;
						dwtmatrixprocess[y][x].r = dwtmatrixtemp[y][first].r + dwtmatrixtemp[y][second].r;
						dwtmatrixprocess[y][x+1].r = dwtmatrixtemp[y][first].r - dwtmatrixtemp[y][second].r;
						dwtmatrixprocess[y][x].g = dwtmatrixtemp[y][first].g + dwtmatrixtemp[y][second].g;
						dwtmatrixprocess[y][x+1].g = dwtmatrixtemp[y][first].g - dwtmatrixtemp[y][second].g;
						dwtmatrixprocess[y][x].b = dwtmatrixtemp[y][first].b + dwtmatrixtemp[y][second].b;
						dwtmatrixprocess[y][x+1].b = dwtmatrixtemp[y][first].b - dwtmatrixtemp[y][second].b;

					}
				}

				dwth *= 2;
				dwtw *= 2;
			}




			for(int y = 0; y < height; y++){

				for(int x = 0; x < width; x++){
					int rrr = (int)Math.round(dctmatrix[y][x].r);
					int ggg = (int)Math.round(dctmatrix[y][x].g);
					int bbb = (int)Math.round(dctmatrix[y][x].b);

					int rr = (int)Math.round(checkbound(dwtmatrixprocess[y][x].r, 255.000, 0.000));
					int gg = (int)Math.round(checkbound(dwtmatrixprocess[y][x].g, 255.000, 0.000));
					int bb = (int)Math.round(checkbound(dwtmatrixprocess[y][x].b, 255.000, 0.000));

					//System.out.println(rr + " " + gg + " " + bb);
					byte dctr = (byte)rrr;
					byte dctg = (byte)ggg;
					byte dctb = (byte)bbb;


					byte dwtr = (byte)rr;
					byte dwtg = (byte)gg;
					byte dwtb = (byte)bb;

					int dctpix = 0xff000000 | ((dctr & 0xff) << 16) | ((dctg & 0xff) << 8) | (dctb & 0xff);
					int dwtpix = 0xff000000 | ((dwtr & 0xff) << 16) | ((dwtg & 0xff) << 8) | (dwtb & 0xff);

					img.setRGB(x,y,dctpix);
					myimg.setRGB(x,y,dwtpix);
				}
			}

			lbIm1.setIcon(new ImageIcon(img));
			lbIm2.setIcon(new ImageIcon(myimg));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


		try {
        Thread.sleep(1000);

      } catch (InterruptedException ie) {
        ie.printStackTrace();
      }
		recursion++;
		if(recursion > 64)
			break;

		}
	}

	public static void main(String[] args) {
		imageReader ren = new imageReader();
		ren.showIms(args);
	}

}