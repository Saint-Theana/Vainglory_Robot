package com.robot;



import java.util.Random;
import java.sql.Connection;  
import java.sql.DriverManager;  
import java.sql.ResultSet;  
import java.sql.Statement;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import org.json.JSONObject;
import org.json.JSONException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import java.sql.*;
import java.awt.*;
import java.awt.font.*;



public class Util
{

	public static String http_dns(String host)
	{  
		try
		{  
			URL lll = new URL("http://119.29.29.29/d?dn="+host);
			HttpURLConnection connection = (HttpURLConnection) lll.openConnection();// 打开连接  
			connection.setRequestMethod("GET");
			connection.connect();// 连接会话  
			// 获取输入流  
			BufferedReader br= new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));  
			String line;  
			StringBuilder sb = new StringBuilder();  
			while ((line = br.readLine()) != null)
			{// 循环读取流  
				sb.append(line);  
			}  
			br.close();// 关闭流
			connection.disconnect();// 断开连接  
			String hosts = sb.toString();
			if(hosts.contains(";")){
				return hosts.split(";")[0];
			}else{
				return hosts;
			}
		}
		catch (Exception e)
		{  
			System.out.println(e.toString());
		}
		return null;
	}

	public static String read_controllers(String group_uin)
	{
		String result = "查询群号: "+Util.ridiculous_uin(group_uin)+"\n已授权列表:\n";
		boolean group_found= false;
		try
		{
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:"+get_file_path_by_path("plugin/database/robot.db"));
			Statement stmt = conn.createStatement();  
			ResultSet rs = stmt.executeQuery("SELECT * FROM Controller");
			while (rs.next()) {  
				if(rs.getString("groupuin").equals(group_uin)){
					group_found = true;
					List<String> controllers = new ArrayList<String>(Arrays.asList(rs.getString("controllers").split(",")));
					for(String controller:controllers){
						result+= Util.ridiculous_uin(controller)+"\n";
					}
				}
			} 
			stmt.close();  
			conn.close();
		}
		catch (ClassNotFoundException e)
		{
			return e.getMessage();

		}
		catch (SQLException e)
		{
			return e.getMessage();
		}
		if (group_found){
			return result;
		}
		return "未查询到该群记录";
	}

	public static boolean is_controller(long group_uin,long sender_uin)
	{
		boolean is = false;
		try
		{
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:"+get_file_path_by_path("plugin/database/robot.db"));
			Statement stmt = conn.createStatement();  
			ResultSet rs = stmt.executeQuery("SELECT * FROM Controller");  
			while (rs.next()) {  
				if(rs.getString("groupuin").equals(String.valueOf(group_uin))){
					List<String> controllers = new ArrayList<String>(Arrays.asList(rs.getString("controllers").split(",")));
					if (controllers.contains(String.valueOf(sender_uin))){
						
						is= true;
					}
				}
			} 
			stmt.close();  
			conn.close();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
			return false;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
		
		return is;
	}

	
	public static String add_controler(String group_uin,String qq_uin)
	{
		boolean group_recorded = false;
		boolean controller_recorded = false;
		String controller_records = "";
		try
		{
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:"+get_file_path_by_path("plugin/database/robot.db"));
			Statement stmt = conn.createStatement();  
			ResultSet rs = stmt.executeQuery("SELECT * FROM Controller");  
			while (rs.next()) {  
				if(rs.getString("groupuin").equals(String.valueOf(group_uin))){
					group_recorded = true;
					List<String> controllers = new ArrayList<String>(Arrays.asList(rs.getString("controllers").split(",")));
					controller_records = rs.getString("controllers");
					if (controllers.contains(qq_uin)){
						controller_recorded = true;
					}
				}
			} 
			stmt.close();  
			conn.close();
		}
		
		catch (ClassNotFoundException e)
		{
			return e.getMessage();
			
		}
		catch (SQLException e)
		{
			return e.getMessage();
		}
		
		if (!group_recorded){
			add_to_database("robot","Controller","groupuin,controllers",group_uin+"\",\""+qq_uin);
			return "添加授权成功";
		}else{
			if(!controller_recorded){
			    update_to_database("robot","Controller","controllers",controller_records+","+qq_uin,"groupuin = "+group_uin);
				return "添加授权成功";
			}else{
				return "授权已存在，无法重新添加";
			}
		}
		
		
	}
	
	public static String remove_controler(String group_uin,String target_uin)
	{boolean removed =false;
		String controller_records = "";
		try
		{
			Class.forName("org.sqlite.JDBC");
			
			Connection conn = DriverManager.getConnection("jdbc:sqlite:"+get_file_path_by_path("plugin/database/robot.db"));
			Statement stmt = conn.createStatement();  
			ResultSet rs = stmt.executeQuery("SELECT * FROM Controller");  
			
			while (rs.next()) {  
				if(rs.getString("groupuin").equals(group_uin)){
					List<String> controllers = new ArrayList<String>(Arrays.asList(rs.getString("controllers").split(",")));
					if(controllers.contains(target_uin)){
						controllers.remove(target_uin);
							removed = true;
					}
					for(String uin:controllers){
						controller_records+=uin+",";
					}
				}
			} 
			stmt.close();  
			conn.close();
		}

		catch (ClassNotFoundException e)
		{
			return e.getMessage();

		}
		catch (SQLException e)
		{
			return e.getMessage();
		}
		if(removed){
			if (controller_records.replaceAll("[,]","").isEmpty()){
				delete_from_database("robot","Controller","groupuin = "+group_uin);
			}else{
			    update_to_database("robot","Controller","controllers",controller_records.replaceAll(",$",""),"groupuin = "+group_uin);
			}
			return "删除成功";
		}
		
		return "删除失败,无法删除空授权";

	}
	
	public static String ridiculous_uin(String qquin)
	{
		String uin = String.valueOf(qquin);
		return uin.replace("0","⓪")
	    	.replace("1","①")
	     	.replace("2","②")
			.replace("3","③")
			.replace("4","④")
			.replace("5","⑤")
			.replace("6","⑥")
			.replace("7","⑦")
			.replace("8","⑧")
			.replace("9","⑨");
	}
	
	
	
	public static int Get_ranke_type(String mode)
	{
		switch(mode){
			case("5v5_pvp_ranked"):{
					return 2;
				}
			case("5v5排位赛"):{
					return 2;
				}
			case("blitz_pvp_ranked"):{
					return 3;
				}
			case("闪电战"):{
					return 3;
				}
			case("ranked"):{
					return 1;
				}
			case("3v3排位赛"):{
					return 1;
				}
			case("casual_aral"):{
					return 4;
				}
			case("大乱斗"):{
					return 4;
				}
			case("5v5_pvp_casual"):{
					return 2;
				}
			case("5v5常规赛"):{
					return 2;
				}
			case("3v3_pvp_casual"):{
					return 1;
				}
			case("3v3常规赛"):{
					return 2;
				}
			default:{
					return 4;
				}
		}
	}
	
	
	
	public static BufferedImage create_xml_img(BufferedImage pressImg,List<List> info_list,Color color, int fontSize) {
		int width = pressImg.getWidth();
		int height = pressImg.getHeight();
		int infos_size = info_list.size();
		int struct_height = height /infos_size;
		int string_start_weight_position = (struct_height - fontSize)/2;
		
		ImageUtil util = new ImageUtil(pressImg);
		
		for (List info_string_list :info_list){
			int info_strings_size = info_string_list.size();
			if(info_strings_size!=0){
				int struct_width = width /info_strings_size;
				int string_start_width_position = 0;
				int string_start_position =string_start_width_position;
				for (Object info_string_obj: info_string_list){
					String info_string = info_string_obj.toString();
					int text_width = getLength(info_string,fontSize);
					int text_height = fontSize;
					int bounus_position = 0;
					if (text_width < struct_width){
						bounus_position = (struct_width -text_width)/2;
					}
					int current_position = bounus_position + string_start_position;
					int current_height = string_start_weight_position;
					util.drawtext(info_string, color,fontSize,current_position, current_height);


					string_start_position =string_start_position +struct_width;

				}
			}
			string_start_weight_position = string_start_weight_position + struct_height;
		}


		//水印文件结束
		return util.get_img_jpg();
    }
	
	public static int getLength(String text,int fontSize) {
		Font font = new Font("宋体", Font.BOLD, fontSize);
		int width = (int) font.getStringBounds(text, new FontRenderContext(font.getTransform(), false, false)).getBounds().getWidth();
		return width;
	}
	
  public static String save_img(BufferedImage img){
	  String file_name = Util.get_random_string(10);
	  String file_path = get_file_path_by_path("plugin/img_output/"+file_name+".png");
	  File outputfile = new File(file_path);
	  try
	  {
		  ImageIO.write(img, "png", outputfile);
	  }
	  catch (IOException e)
	  {
		  System.out.println(e.getMessage());
	  }
      return file_path;
  }
	
	public static String save_img_jpg(BufferedImage img){
		String file_name = Util.get_random_string(10);
		String file_path = get_file_path_by_path("plugin/img_output/"+file_name+".jpg");
		File outputfile = new File(file_path);
		try
		{
			ImageIO.write(img, "jpg", outputfile);
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
		return file_path;
	}
	
  public static String get_file_path_by_path(String path){
	  
	  File directory = new File("");
	  String exact_directory = "";
	  try
	  {

		  exact_directory  = directory.getCanonicalPath();
	  }
	  catch (IOException e)
	  {
		  System.out.println(e.getMessage());
	  }
	  return exact_directory + "/" + path;
	  }
	  
	  
  public static String Match_SkyTier(int ranked_score)
	{
		int Skytier = 0;
		String Title = "木有";
		String color = "#8B4513";
		if (ranked_score >= 400)
		{
			Skytier = 2;
			Title = "初出茅庐";
			color = "#8B4513";
		}
		if (ranked_score >= 467)
		{
			Skytier = 3;
			color = "#C0C0C0";
		}
		if (ranked_score >= 534)
		{
			Skytier = 4;
			color = "#FFD700";
		}
		if (ranked_score >= 600)
		{
			Skytier = 5;
			Title = "逐步成长";
			color = "#8B4513";
		}
		if (ranked_score >= 667)
		{
			Skytier = 6;
			color = "#C0C0C0";
		}
		if (ranked_score >= 734)
		{
			Skytier = 7;
			color = "#FFD700";
		}
		if (ranked_score >= 800)
		{
			Skytier = 8;
			Title = "铜头铁臂";
			color = "#8B4513";
		}
		if (ranked_score >= 867)
		{
			Skytier = 9;
			color = "#C0C0C0";
		}
		if (ranked_score >= 934)
		{
			Skytier = 10;
			color = "#FFD700";
		}
		if (ranked_score >= 1000)
		{
			Skytier = 11;
			Title = "值得一战";
			color = "#8B4513";
		}
		if (ranked_score >= 1067)
		{
			Skytier = 12;
			color = "#C0C0C0";
		}
		if (ranked_score >= 1134)
		{
			Skytier = 13;
			color = "#FFD700";
		}
		if (ranked_score >= 1200)
		{
			Skytier = 14;
			Title = "深藏不露";
			color = "#8B4513";
		}
		if (ranked_score >= 1267)
		{
			Skytier = 15;
			color = "#C0C0C0";
		}
		if (ranked_score >= 1334)
		{
			Skytier = 16;
			color = "#FFD700";
		}
		if (ranked_score >= 1400)
		{
			Skytier = 17;
			Title = "名不虚传";
			color = "#8B4513";
		}
		if (ranked_score >= 1467)
		{
			Skytier = 18;
			color = "#C0C0C0";
		}
		if (ranked_score >= 1534)
		{
			Skytier = 19;
			color = "#FFD700";
		}
		if (ranked_score >= 1600)
		{
			Skytier = 20;
			Title = "炉火纯青";
			color = "#8B4513";
		}
		if (ranked_score >= 1667)
		{
			Skytier = 21;
			color = "#C0C0C0";
		}
		if (ranked_score >= 1734)
		{
			Skytier = 22;
			color = "#FFD700";
		}
		if (ranked_score >= 1800)
		{
			Skytier = 23;
			Title = "神乎其技";
			color = "#8B4513";
		}
		if (ranked_score >= 1867)
		{
			Skytier = 24;
			color = "#C0C0C0";
		}
		if (ranked_score >= 1934)
		{
			Skytier = 25;
			color = "#FFD700";
		}
		if (ranked_score >= 2000)
		{
			Skytier = 26;
			Title = "登峰造极";
			color = "#8B4513";
		}
		if (ranked_score >= 2134)
		{
			Skytier = 27;
			color = "#C0C0C0";
		}
		if (ranked_score >= 2267)
		{
			Skytier = 28;
			color = "#FFD700";
		}
		if (ranked_score >= 2400)
		{
			Skytier = 29;
			Title = "至尊荣耀";
			color = "#8B4513";
		}
		if (ranked_score >= 2600)
		{
			Skytier = 30;
			color = "#C0C0C0";
		}
		if (ranked_score >= 2800)
		{
			Skytier = 31;
			color = "#FFD700";
		}
		return Skytier + " " + Title +" "+color;
	}
	
	public static String match_translate(String mode)
	{
		switch(mode){
			case("5v5_pvp_ranked"):{
				return "5v5排位赛";
			}
			case("blitz_pvp_ranked"):{
					return "闪电战";
				}
			case("ranked"):{
					return "3v3排位赛";
				}
			case("casual_aral"):{
					return "大乱斗";
				}
			case("5v5_pvp_casual"):{
					return "5v5常规赛";
				}
			case("3v3_pvp_casual"):{
					return "3v3排位赛";
				}
			default:{
				return "其他比赛";
			}
		}
	}
	
  public static String hero_translate(String hero)
	{
		String hero_json = "{ \"Ylva\":\"伊尔瓦\", \"Magnus\":\"妈个那屎\", \"Yates\":\"雅特斯\", \"Silvernail\":\"银色大钉钉\",  \"Samuel\":\"塞缪尔\",   \"Alpha\":\"阿尔法\",   \"Adagio\":\"天使\",   \"Ardan\":\"二蛋\",   \"Blackfeather\":\"黑鱼\",   \"Catherine\":\"女警\",   \"Celeste\":\"星妈\",   \"Fortress\":\"狗子\",   \"Glaive\":\"扶伤豹\",   \"Joule\":\"朱尔\",   \"Kestrel\":\"鹰眼\",   \"Koshka\":\"猫女\",   \"Krul\":\"鬼剑\",   \"Lance\":\"光头\",   \"Lyra\":\"莱拉\",   \"Ozo\":\"猴子\",   \"Petal\":\"花花\",   \"Phinn\":\"鱼人\",   \"Reim\":\"老头\",   \"Ringo\":\"酒枪\",   \"Rona\":\"罗娜\",   \"SAW\":\"索尔\",   \"Skaarf\":\"肥蛆\",   \"Skye\":\"撕开衣\",   \"Taka\":\"塔卡\",   \"Vox\":\"舞司\",   \"Gwen\":\"格温\",   \"Kinetic\":\"基尼\",   \"Anka\":\"安卡\",   \"Reza\":\"雷萨\",   \"Malene\":\"梅兰妮\",   \"Tony\":\"拖泥\",   \"Grace\":\"奶锤\",   \"Idris\":\"伊德瑞\",   \"Baron\":\"巴隆\",   \"Kensei\":\"剑圣\",   \"Varya\":\"瓦尼娅\",   \"Baptiste\":\"巴蒂斯特\",   \"Grumpjaw\":\"大嘴\",   \"Lorelai\":\"洛姬\",   \"Flicker\":\"小精灵\",   \"Churnwalker\":\"沃克尔\"}";
		try
		{
			JSONObject json=new JSONObject(hero_json);
			return json.getString(hero);

		}
		catch (JSONException e)
		{}
		return "新英雄";
	}
	
  public static String translate_game_mode(String match)
	{
		String game_mode_json = "{   \"3v3排位\":\"Ranked\",   \"3v3匹配\":\"Casual\",     \"5v5排位\":\"Ranked%205v5\",    \"5v5匹配\":\"Casual%205v5\",     \"大乱斗\":\"Battle%20Royale\",       \"闪电战\":\"Blitz\"   }";
		try
		{
			JSONObject json=new JSONObject(game_mode_json);
			return json.getString(match);

		}
		catch (JSONException e)
		{}
		return "其他比赛";
	}
	
	public static String Post(String url)
	{  
		try
		{  
			URL lll = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) lll.openConnection();// 打开连接  
			connection.setRequestMethod("POST");
			connection.connect();// 连接会话  
			// 获取输入流  
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));  
			String line;  
			StringBuilder sb = new StringBuilder();  
			while ((line = br.readLine()) != null)
			{// 循环读取流  
				sb.append(line);  
			}  
			br.close();// 关闭流
			connection.disconnect();// 断开连接  
			return sb.toString();
		}
		catch (Exception e)
		{  
			System.out.println(e.toString());
		}
		return null;
	}
	
	public static String Auth_Curl(String url, String vg_api_key) 
	{

		try
		{  
			URL lll = new URL(url);    //把字符串转换为URL请求地址  
			HttpURLConnection connection = (HttpURLConnection) lll.openConnection();// 打开连接  
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Authorization", "Bearer " + vg_api_key);
			connection.setRequestProperty("Accept", "application/vnd.api+json");
			connection.connect();// 连接会话  
			// 获取输入流  
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));  
			String line;  
			StringBuilder sb = new StringBuilder();  
			while ((line = br.readLine()) != null)
			{// 循环读取流  
				sb.append(line);  
			}  
			br.close();// 关闭流  
			connection.disconnect();// 断开连接  
			return sb.toString();
		}
		catch (Exception e)
		{  
			e.printStackTrace();
		}
		return null;
	}  
	
  public static String post_with_data(String url, String data)
	{  
		try
		{
			URL lll = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) lll.openConnection();// 打开连接  
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Accept", "*/*");
			connection.setDoOutput(true);
			connection.setRequestProperty("Referer", "https://music.163.com/m/song?id=16431842");
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");
			connection.setRequestProperty("Origin","http://music.163.com");
			connection.setRequestProperty("Connection","keep-alive");
			connection.setRequestProperty("Accept-Language","zh-CN,zh;q=0.9,en;q=0.8,zh-TW;q=0.7");
			connection.connect();// 连接会话  
			// 获取输入流  
			PrintWriter writer = new PrintWriter(connection.getOutputStream());
			writer.print(data);                                    
			writer.flush();
			BufferedReader br= new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));  
			String line;  
			StringBuilder sb = new StringBuilder();  
			while ((line = br.readLine()) != null)
			{// 循环读取流  
				sb.append(line);  
			}  
			br.close();// 关闭流
			connection.disconnect();// 断开连接  
			return sb.toString();
		}
		catch (Exception e)
		{  
			System.out.println(e.toString());
		}
		return null;
	}
	
 public static String curl_with_referer(String url, String referer)
	{  
		try
		{  
			URL lll = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) lll.openConnection();// 打开连接  
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Referer", referer);
			connection.connect();// 连接会话  
			// 获取输入流  
			BufferedReader br= new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));  
			String line;  
			StringBuilder sb = new StringBuilder();  
			while ((line = br.readLine()) != null)
			{// 循环读取流  
				sb.append(line);  
			}  
			br.close();// 关闭流
			connection.disconnect();// 断开连接  
			return sb.toString();
		}
		catch (Exception e)
		{  
			System.out.println(e.toString());
		}
		return null;
	}
	
 public static String get_redirected_url(String url){
		String location =null;
		try {  
            URL serverUrl = new URL(url);  
            HttpURLConnection connection = (HttpURLConnection) serverUrl.openConnection();  
            connection.setRequestMethod("GET");  
            // 必须设置false，否则会自动redirect到Location的地址  
            connection.setInstanceFollowRedirects(false);  
            connection.addRequestProperty("Accept-Charset", "UTF-8;");  
            connection.addRequestProperty("User-Agent","Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.8) Firefox/3.6.8");
            connection.connect();  
            location = connection.getHeaderField("Location");  
            serverUrl = new URL(location);  
            connection = (HttpURLConnection) serverUrl.openConnection();  
            connection.setRequestMethod("GET");  
            connection.addRequestProperty("Accept-Charset", "UTF-8;");  
            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.8) Firefox/3.6.8");  
            connection.addRequestProperty("Referer", "http://zuidaima.com/");  
            connection.connect();

        } catch (Exception e) {  
            e.printStackTrace();  
        }  
		return location;
	}
	
public static String Curl(String url)
	{
		try
		{
			InputStream is=new URL(url).openStream();
			ByteArrayOutputStream buffer=new ByteArrayOutputStream();
			int b=-1;
			while ((b = is.read()) != -1)
				buffer.write(b);
			return new String(buffer.toByteArray());
		}
		catch (MalformedURLException e)
		{
		  System.out.println(e.toString());
		}
		catch (IOException e)
		{
		System.out.println(e.toString());
		}
		return null;
	}
	
public static String get_random_string(int length){
		//定义一个字符串（A-Z，a-z，0-9）即62位；
		String str="zxcvbnmlkjhgfdsaqwertyuiopjiduhjdiubfkoYHHVTtK52qASDVJInhsybnfk8fjnfkfibnfybsnsk";
		//由Random生成随机数
        Random random=new Random();  
        StringBuffer sb=new StringBuffer();
        //长度为几就循环几次
        for(int i=0; i<length; ++i){
			//产生0-61的数字
			int number=random.nextInt(62);
			//将产生的数字通过length次承载到sb中
			sb.append(str.charAt(number));
        }
        //将承载的字符转换成字符串
        return sb.toString();
	}

    
  public static List<String> get_database_contain(String Database,String Table,String key){
	 File directory = new File("");
		String exact_directory = "";
		List<String> Value_list = new ArrayList<>();
		String value = null;
		try
		{
		  Class.forName("org.sqlite.JDBC");
		  exact_directory  = directory.getCanonicalPath();
		Connection conn = DriverManager.getConnection("jdbc:sqlite:"+get_file_path_by_path("plugin/database/"+Database+".db"));
   Statement stmt = conn.createStatement();  
		ResultSet rs = stmt.executeQuery("SELECT * FROM "+Table);  
        while (rs.next()) {  
            value=rs.getString(key);
            Value_list.add(value);
        }  
        stmt.close();  
        conn.close();
		
		
		}
		catch (ClassNotFoundException e)
		{
		  e.printStackTrace();
		}
	  catch (IOException e)
	  {
		 e.printStackTrace();
	  }
	  catch (SQLException e)
	  {
		  e.printStackTrace();
	  }
		return Value_list;
		
	}
	
	
	public static List<List> get_database_contain(String Database,String Table,String key1,String key2){
		File directory = new File("");
		String exact_directory = "";
		String value = null;
		List<List> content_list = new ArrayList<>();
		try
		{
			Class<?> forName;
			forName = Class.forName("org.sqlite.JDBC");
			exact_directory  = directory.getCanonicalPath();
			Connection conn = DriverManager.getConnection("jdbc:sqlite:"+get_file_path_by_path("plugin/database/"+Database+".db"));
			Statement stmt = conn.createStatement();  
			ResultSet rs = stmt.executeQuery("SELECT * FROM "+Table);  

			String value1 = "";
			String value2 = "";

			while (rs.next()) {  
				List<String> values = new ArrayList<>();
				value1 = rs.getString(key1);
				value2 = rs.getString(key2);
				values.add(value1);
				values.add(value2);
	         	content_list.add(values);
			} 
			stmt.close();  
			conn.close();


		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return content_list;

	}
	
	public static void add_to_database(String Database,String table,String table_message,String values){
	System.out.println("values"+values);
	 File directory = new File("");
		String exact_directory = "";
		try
		{
		Class.forName("org.sqlite.JDBC");
		exact_directory  = directory.getCanonicalPath();
		Connection conn = DriverManager.getConnection("jdbc:sqlite:"+get_file_path_by_path("plugin/database/"+Database+".db"));  
		Statement stmt = conn.createStatement();
		String sql = "INSERT INTO "+table+" ("+table_message+") " + "VALUES (\""+values+"\");";
		stmt.executeUpdate(sql);
		stmt.close();  
  conn.close();
		
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	
	}
	
	public static void update_to_database(String Database,String table,String key,String value,String whereis){
	System.out.println(value);
	 File directory = new File("");
		String exact_directory = "";
		try
		{
		Class.forName("org.sqlite.JDBC");
		exact_directory  = directory.getCanonicalPath();
		Connection conn = DriverManager.getConnection("jdbc:sqlite:"+get_file_path_by_path("plugin/database/"+Database+".db"));
		Statement stmt = conn.createStatement();
		String sql = "UPDATE "+table+" set "+key+" = \""+value+"\" where "+whereis+";";
		System.out.println(sql);
		stmt.executeUpdate(sql);
		stmt.close();  
  conn.close();
		
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
		
	public static void delete_from_database(String Database,String table,String whereis){
	 File directory = new File("");
		String exact_directory = "";
		try
		{
		Class.forName("org.sqlite.JDBC");
		exact_directory  = directory.getCanonicalPath();
			Connection conn = DriverManager.getConnection("jdbc:sqlite:"+get_file_path_by_path("plugin/database/"+Database+".db"));
		Statement stmt = conn.createStatement();
		String sql = "DELETE from "+table+" where "+whereis+";";
		stmt.executeUpdate(sql);
		stmt.close();  
  conn.close();
		
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public final static BufferedImage get_img(String file_name){
	System.out.println(file_name);
	BufferedImage img_to_send = null;
	File directory = new File("");
		String exact_directory = "";
	try
	{
	  exact_directory  = directory.getCanonicalPath();
		img_to_send = ImageIO.read(new File(get_file_path_by_path("plugin/img/"+file_name)));
	}
	catch(Exception e){
	  System.out.println(e.toString());
	 }
	return img_to_send;
	}
	
	
	
	public final static BufferedImage get_random_img(String path_name){
	BufferedImage img_to_send = null;
	File directory = new File("");
		String exact_directory = "";
	try
	{
	  exact_directory  = directory.getCanonicalPath();
		File img_path = new File(get_file_path_by_path("plugin/img/"+path_name));
		String[] img_list = img_path.list();
		List list = Arrays.asList(img_list);
		Collections.shuffle(list);
		String[] final_img_list =(String[]) list.toArray();
 	 img_to_send = ImageIO.read(new File(get_file_path_by_path("plugin/img/"+path_name+"/"+final_img_list[0])));
	}
	catch(Exception e){
	  System.out.println(e.toString());
	 }
	return img_to_send;
	}
	
	public static HostnameVerifier hv = new HostnameVerifier() {
        public boolean verify(String urlHostName, SSLSession session)
		{
            System.out.println("Warning: URL Host: " + urlHostName + " vs. "
                               + session.getPeerHost());
            return true;
        }
    };

  public static void trustAllHttpsCertificates() throws Exception
	{
		javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
		javax.net.ssl.TrustManager tm = new miTM();
		trustAllCerts[0] = tm;
		javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext
			.getInstance("SSL");
		sc.init(null, trustAllCerts, null);
		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc
																	.getSocketFactory());
	}
	
	public static class miTM implements javax.net.ssl.TrustManager,
	javax.net.ssl.X509TrustManager
	{
		public java.security.cert.X509Certificate[] getAcceptedIssuers()
		{
			return null;
		}

		public static boolean isServerTrusted(
			java.security.cert.X509Certificate[] certs)
		{
			return true;
		}

		public static boolean isClientTrusted(
			java.security.cert.X509Certificate[] certs)
		{
			return true;
		}

		public void checkServerTrusted(
			java.security.cert.X509Certificate[] certs, String authType)
		throws java.security.cert.CertificateException
		{
			return;
		}

		public void checkClientTrusted(
			java.security.cert.X509Certificate[] certs, String authType)
		throws java.security.cert.CertificateException
		{
			return;
		}
	}
	
	
	
}
