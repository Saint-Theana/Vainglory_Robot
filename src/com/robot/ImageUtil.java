package com.robot;


import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.imageio.stream.*;



public class ImageUtil{
	
	public int imgwidth;
	public int imgheight;
	public BufferedImage img;
	private Graphics graphic;
	public ImageUtil(BufferedImage _img){
		this.img = _img;
		this.imgwidth = this.img.getWidth();
		this.imgheight = this.img.getHeight();
		this.graphic = this.img.getGraphics();
		if(this.graphic==null){
			this.img.createGraphics();
		}else{
			this.graphic.drawImage(img, 0, 0, this.imgwidth, this.imgheight, null);
		}
	}

	public void drawLine(float width,Color color,int start_x, int start_y,float end_x, int end_y) {
		Graphics2D g2 = (Graphics2D)this.graphic;
		
		g2.setStroke(new BasicStroke(width, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
		g2.setColor(color);
		
        g2.drawLine(start_x, start_y,(int)end_x,end_y);
    }

	public void update_img(BufferedImage _img)
	{
		this.img = _img;
		this.imgwidth = this.img.getWidth();
		this.imgheight = this.img.getHeight();
		this.graphic = this.img.getGraphics();
		if(this.graphic==null){
			this.img.createGraphics();
		}else{
			this.graphic.drawImage(img, 0, 0, this.imgwidth, this.imgheight, null);
		}
	}
	
	public BufferedImage get_img(){
		this.graphic.dispose();
		return this.img;
		
	}
	
	public BufferedImage get_img_jpg(){
		
		BufferedImage bufferedImage = new BufferedImage(this.imgwidth, this.imgheight, BufferedImage.TYPE_INT_RGB);
		
		Graphics2D g = bufferedImage.createGraphics();
		g.drawImage(this.img, null, null);
		this.graphic.dispose();
		g.dispose();
		return bufferedImage;

	}
	
	
	
	
	public void createWaterMaskRightTop(
		BufferedImage watermark,
		int paddingRight, int paddingTop) {
        drawwater(watermark,
							  this.imgwidth - watermark.getWidth() - paddingRight,
							  paddingTop);
    }
	public void createWaterMaskRightBottom(
		BufferedImage watermark,
		int paddingRight, int paddingBottom) {
        drawwater(watermark,
							  this.imgwidth - watermark.getWidth() - paddingRight,
							  this.imgheight - watermark.getHeight() - paddingBottom);
    }
	
	public void drawTextToRightTop(String text,
								   int size, Color color, int paddingRight, int paddingTop) {
        
									   
        drawtext(text,
				color,size, this.imgwidth -  Util.getLength(text,size) - paddingRight,
						 paddingTop);
    }
	
	public void drawTextToRightBottom(String text,
									  int size, Color color, int paddingRight, int paddingBottom) {
        
        drawtext(text,
				 color,size, this.imgwidth -  Util.getLength(text,size) -  paddingRight,
						 this.imgheight -  paddingBottom-size);
    }
	public void drawTextToLeftBottom(String text,
									 int size, Color color, int paddingLeft, int paddingBottom) {
        
        drawtext(text,color,size,
						 paddingLeft,
						 this.imgheight - paddingBottom-size);
    }
	
	public void drawTextToLeftTop(String text,
								  int size, Color color, float paddingLeft, int paddingTop) {

		
									  
        drawtext(text, color,size
						 ,paddingLeft,
						 paddingTop);

    }
	public void createWaterMaskLeftTop(BufferedImage watermark,
									   int paddingLeft, int paddingTop) {
        drawwater(watermark,
							  paddingLeft,
							 paddingTop);
    }
	
	
	public void drawwater(BufferedImage pressImg2, int x, int y) {
        try {
            //目标文件
        
            int wideth = pressImg2.getWidth(null);//获得水印图真实宽度,无图返回-1
            int height = pressImg2.getHeight(null);//获得水印图真实高度,无图返回-1
            x=x>=0?x:(x+this.imgwidth-wideth);
            y=y>=0?y:(y+this.imgheight-height);
            this.graphic.drawImage(pressImg2, x, y, wideth, height, null);
            //水印文件结束
         
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

	public void drawtext(String message_string,Color color, int fontSize,float x, float y) {
        try {
            
            //水印文件


            this.graphic.setFont(new Font("宋体", Font.BOLD, fontSize));
            this.graphic.setColor(color);
			
            int text_width =  Util.getLength(message_string,fontSize);
			int text_height = fontSize;
            x=x>=0?x:(x+this.imgwidth-text_width);
            y=y>=0?y:(y+this.imgheight-text_height);
			this.graphic.drawString(message_string, (int)x, (int)(y + text_height));
            //水印文件结束
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

	
    



	
}
