package com.robot;


import java.awt.image.BufferedImage;
import java.awt.Color;
import javax.imageio.ImageIO;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.List;
import java.util.Date;
import java.util.ArrayList;
import javax.net.ssl.HttpsURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.*;
import java.util.*;
import java.net.*;
import java.io.*;
import java.sql.*;

class Data_roles
{
	public double kda;
	public int games; 
	public int wins;
	public int loss;
	public double winRate;
	public double avgKills;
	public double avgDeaths;
	public double avgAssists;
	public  Data_roles()
	{

	}


}
public class VaingloryFactory
{

	public static String Sky_tier(String player_name, String player_region)
	{
		int blitz= 0;
		int ranked = 0;
		int ranked_5v5 = 0;
		String blitz_Sky_info = "";
		String blitz_Sky_tier = "";
		String blitz_Sky_title = "";
		String ranked_Sky_info = "";
		String ranked_Sky_tier = "";
		String ranked_Sky_title = "";
		String ranked_5v5_Sky_info = "";
		String ranked_5v5_Sky_tier = "";
		String ranked_5v5_Sky_title = "";
		String vg_api_key ="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI2NmFiZjc5MC0zNzQxLTAxMzctZWY5YS0wYTU4NjQ2MTRkN2MiLCJpc3MiOiJnYW1lbG9ja2VyIiwiaWF0IjoxNTU0MTg3Nzc1LCJwdWIiOiJzZW1jIiwidGl0bGUiOiJ2YWluZ2xvcnkiLCJhcHAiOiJoa2s2cnNjIiwic2NvcGUiOiJjb21tdW5pdHkiLCJsaW1pdCI6MTB9.Tz2gSpXiGieLp-KPWzX_MiKx8dk0J2TkYWnqeKHVOm4";


		String message = Util.Auth_Curl("https://api.dc01.gamelockerapp.com/shards/cn/players?filter[playerNames]=" + player_name, vg_api_key);
		if (message == null)
		{
			return "未查询到该玩家";
		}
		else
		{
			try
			{
				JSONObject json = new JSONObject(message);
				JSONObject data = json.getJSONArray("data").getJSONObject(0).getJSONObject("attributes").getJSONObject("stats").getJSONObject("rankPoints");
				blitz = data.getInt("blitz");
				ranked = data.getInt("ranked");
				ranked_5v5 = data.getInt("ranked_5v5");
			}
			catch (JSONException e)
			{
				System.out.println(e.toString());
			}

			if (blitz == 0 || blitz == -1)
			{
				blitz_Sky_tier = "1";
				blitz_Sky_title = "该咸鱼还没段位";
			}
			else
			{
				blitz_Sky_info = Util.Match_SkyTier(blitz);
				blitz_Sky_tier = blitz_Sky_info.split(" ")[0];
				blitz_Sky_title = blitz_Sky_info.split(" ")[1];
			}
			if (ranked == 0 || ranked == -1)
			{
				ranked_Sky_tier = "1";
				ranked_Sky_title = "该咸鱼还没段位";
			}
			else
			{
				ranked_Sky_info = Util.Match_SkyTier(ranked);
				ranked_Sky_tier = ranked_Sky_info.split(" ")[0];
				ranked_Sky_title = ranked_Sky_info.split(" ")[1];
			}
			if (ranked_5v5 == 0 || ranked_5v5 == -1)
			{
				ranked_5v5_Sky_tier = "1";
				ranked_5v5_Sky_title = "该咸鱼还没段位";
			}
			else
			{
				ranked_5v5_Sky_info = Util.Match_SkyTier(ranked_5v5);
				ranked_5v5_Sky_tier = ranked_5v5_Sky_info.split(" ")[0];
				ranked_5v5_Sky_title = ranked_5v5_Sky_info.split(" ")[1];
			}

			List<List> info_list = new ArrayList<>();
			List<String> info1 =new ArrayList<String>();
			info1.add("服务大区: " + player_region + " ID: " + player_name);
			List<String> info2 =new ArrayList<String>();
			info2.add("3v3段位");
			info2.add("5v5段位");
			info2.add("闪电战段位");
			List<String> info3 =new ArrayList<String>();
			List<String> info4 =new ArrayList<String>();
			List<String> info5 =new ArrayList<String>();
			List<String> info6 =new ArrayList<String>();
			info6.add(ranked_Sky_title);
			info6.add(ranked_5v5_Sky_title);
			info6.add(blitz_Sky_title);
			List<String> info7 =new ArrayList<String>();
			info7.add(String.valueOf(ranked));
			info7.add(String.valueOf(ranked_5v5));
			info7.add(String.valueOf(blitz));
			info_list.add(info1);
			info_list.add(info2);
			info_list.add(info3);
			info_list.add(info4);
			info_list.add(info5);
			info_list.add(info6);
			info_list.add(info7);

			BufferedImage ranked_Sky_tier_img = Util.get_img("tier" + ranked_Sky_tier + ".png");
			BufferedImage ranked_5v5_Sky_tier_img = Util.get_img("tier" + ranked_5v5_Sky_tier + ".png");	
			BufferedImage blitz_Sky_tier_img = Util.get_img("tier" + blitz_Sky_tier + ".png");

			BufferedImage logo_img = Util.get_img("xml_ranked.png");
			BufferedImage ground_img = Util.get_random_img("Sky_Tier");
			ImageUtil util = new ImageUtil(ground_img);
			util.drawwater(logo_img, 0, 0);
			util.drawwater(ranked_Sky_tier_img, 0, 300);
			util.drawwater(ranked_5v5_Sky_tier_img, 450, 300);
			util.drawwater(blitz_Sky_tier_img, 900, 300);
			BufferedImage createdimg = util.get_img();
			createdimg = Util.create_xml_img(createdimg, info_list, new Color(139, 0, 0), 40);
			String file_path = Util.save_img_jpg(createdimg);
			return file_path;
		}
	}
	
	
	public static String Match_super_detail_img( String player_name, int page)
	{
		try
		{
			Util.trustAllHttpsCertificates();
			HttpsURLConnection.setDefaultHostnameVerifier(Util.hv);
		}
		catch (Exception e)
		{}
		BufferedImage ground_bitmap = Util.get_img("super_detail_5v5_ground.png");
		ImageUtil imgutil = new ImageUtil(ground_bitmap);
		Color font_color = new Color(0,0,0);
	
			//String tier = null

			DecimalFormat df = new DecimalFormat("0.0");		
			StringBuilder left_list = new StringBuilder("");
			StringBuilder right_list = new StringBuilder("");
		try
		{
			
			Class.forName("org.mariadb.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/VaingloryData","root","1721115102Qq");
			Statement stmt = conn.createStatement();  
			ResultSet rs = stmt.executeQuery("SELECT * FROM Matches where player_names like \"%," + player_name + ",%\" or player_names like \"" + player_name + ",%\" or player_names like \"%," + player_name + "\" order by game_time desc");
			Connection conn_vip = DriverManager.getConnection("jdbc:mariadb://localhost:3306/vipaccount","root","1721115102Qq");
			Statement stmt_vip = conn_vip.createStatement();  
			stmt_vip.executeUpdate("insert into players(name) values(\"" + player_name + "\");");
			stmt_vip.close();
			conn_vip.close();
			int jump =0;
			while (jump < page)
			{
				rs.next();
				jump += 1;
			}
			if (!rs.next())
			{
				return "未查询到数据,可能的原因:1.该玩家不存在,2.数据库未记录该玩,3.该页数超出最大已记录数据;已将该玩家写入立刻更新列表.";
			}
				JSONObject root = new JSONObject(rs.getString("data").replaceAll("'([A-Za-z ]*)'([A-Za-z ]*)'", "'$1$2'"));

				String gamemode = Util.match_translate(root.getString("gameMode").replace(" ", "_"));
				int Ranke_type = Util.Get_ranke_type(gamemode);
				String createdAt = root.getString("createdAt").replaceAll("[A-Z]", " ");
				JSONArray player_list = root.getJSONArray("actors");
				JSONArray rosters = root.getJSONArray("rosters");
				String game_id = root.getString("id");
				String telemetry = Util.Curl("https://"+Util.http_dns("api.vgpro.gg")+"/matches/"+game_id+"/cn/telemetry");
				JSONObject telemetry_json = new JSONObject(telemetry);
				JSONObject blue_telemetry_list = telemetry_json.getJSONObject("facts").getJSONObject("blue");
				JSONObject red_telemetry_list = telemetry_json.getJSONObject("facts").getJSONObject("red");
			JSONObject left_telemetry_list = null;
			JSONObject right_telemetry_list = null;
			JSONArray lefts_list;
			JSONArray rights_list;
				Iterator iterator = blue_telemetry_list.keys();
				long blue_damage = 0;
				long blue_healed = 0;
				long blue_taken = 0;
			long left_damage = 0;
				long left_healed = 0;
				long  left_taken = 0;
				while(iterator.hasNext()){
					String key = (String) iterator.next();
					if (!key.equals("KindredSocialPingsManifest")){
						blue_damage = blue_damage+blue_telemetry_list.getJSONObject(key).getLong("damage");
						blue_healed = blue_healed+blue_telemetry_list.getJSONObject(key).getLong("healed");
						blue_taken = blue_taken+blue_telemetry_list.getJSONObject(key).getLong("taken");
					}
				}
				Iterator iterator1 = red_telemetry_list.keys();
				long red_damage = 0;
				long red_healed = 0;
				long red_taken = 0;
			long right_damage = 0;
			long right_healed = 0;
			long  right_taken = 0;
				while(iterator1.hasNext()){
					String key = (String) iterator1.next();
					if (!key.equals("KindredSocialPingsManifest")){
						red_damage = red_damage+red_telemetry_list.getJSONObject(key).getLong("damage");
						red_healed = red_healed+red_telemetry_list.getJSONObject(key).getLong("healed");
						red_taken = red_taken+red_telemetry_list.getJSONObject(key).getLong("taken");
					}
				}
			String  letf_team_kill ="";
			String  right_team_kill ="";
			String  letf_team_turret ="";
			String  right_team_turret ="";
			String  letf_team_gold ="";
			String  right_team_gold ="";
			
			for (int time = 0; time < rosters.length(); time++)
			{
				JSONObject data = rosters.getJSONObject(time);
				if (data.getBoolean("won"))
				{
					if(data.getString("side").equals("left/blue")){
						left_telemetry_list=blue_telemetry_list;
						right_telemetry_list=red_telemetry_list;
						left_damage = blue_damage;
						left_healed = blue_healed;
						left_taken = blue_taken;
						right_damage = red_damage;
						right_healed = red_healed;
						right_taken = red_taken;
					}else{
						left_telemetry_list=red_telemetry_list;
						right_telemetry_list=blue_telemetry_list;
						left_damage = red_damage;
						left_healed = red_healed;
						left_taken = red_taken;
						right_damage = blue_damage;
						right_healed = blue_healed;
						right_taken = blue_taken;
					}
					letf_team_kill = (String.valueOf(data.getInt("heroKills")));
					letf_team_turret = (String.valueOf(data.getInt("turretKills")));
					letf_team_gold =  (String.valueOf(data.getInt("gold") / 1000));
				}
				else
				{
					right_team_kill = (String.valueOf(data.getInt("heroKills")));
					right_team_turret = (String.valueOf(data.getInt("turretKills")));
					right_team_gold =  (String.valueOf(data.getInt("gold") / 1000));
				}
			}
			for (int time =0 ; time < player_list.length(); time++)
			{
				JSONObject player=player_list.getJSONObject(time);
				if (player.getBoolean("winner"))
				{
					left_list.append(player + ",");
				}
				else
				{
					right_list.append(player + ",");
				}
			}
			left_list.append("@@@@");
			right_list.append("@@@@");
			lefts_list = new JSONArray("[" + left_list.toString().replace(",@@@@", "") + "]");
			rights_list = new JSONArray("[" + right_list.toString().replace(",@@@@", "") + "]");
				if(player_list.length()<=6){
					imgutil.update_img(Util.get_img("super_detail_3v3_ground.png"));
				}
			if(player_list.length()<=2){
				imgutil.update_img(Util.get_img("super_detail_1v1_ground.png"));
			}
			int struct_possition =0;
			imgutil.drawwater(Util.get_img("one_roster.png"),0,0);
			imgutil.drawTextToLeftTop( "日期 " + new SimpleDateFormat("yyyy-MM-dd_HH:mm").format(new Date(rs.getLong("game_time"))).replaceAll("20[0-9]*-", "")+ " "+String.valueOf((int)root.getInt("duration") / 60) + "分钟", 45, font_color, 10, 30);
			imgutil.drawTextToLeftTop(gamemode, 45,font_color, 10, 95);
			imgutil.drawTextToLeftTop(letf_team_kill, 60,font_color, 910, struct_possition+60);
			imgutil.drawTextToLeftTop(letf_team_turret, 40,font_color,680, struct_possition+110);
			imgutil.drawTextToLeftTop(letf_team_gold+"K", 40,font_color, 700, struct_possition+55);
			
			struct_possition +=193;
			//蓝方
			for(int i =0;i<lefts_list.length();i+=1){
				JSONObject player = lefts_list.getJSONObject(i);
				BufferedImage y  = Util.get_img("one_player.png");
				imgutil.drawwater(y,0,struct_possition);
				BufferedImage hero_bitmap = Util.get_img(player.getString("hero").toLowerCase()+".png");
				imgutil.createWaterMaskLeftTop(hero_bitmap, 35, struct_possition);
				imgutil.drawTextToLeftTop(player.getString("name"), 35,font_color, 0, struct_possition+140);
				imgutil.drawTextToLeftTop(String.valueOf(player.getInt("kills")) + "/" + String.valueOf(player.getInt("deaths")) + "/" + String.valueOf(player.getInt("assists")), 40,font_color, 170, struct_possition+90);
				JSONArray items_list = player.getJSONArray("items");
				if (items_list.length() != 0)
				{
					int item_start_position = 170;
					for (int time_1 = 0; time_1 < items_list.length(); time_1++)
					{String item = items_list.getString(time_1).replace(" ", "").replace("'", "").toLowerCase();
						if (!item.equals("healingflask") && !item.equals("visiontotem"))
						{
							BufferedImage item_bitmap = Util.get_img(item+ ".png");
							imgutil.createWaterMaskLeftTop(item_bitmap, item_start_position, struct_possition+10);
							item_start_position = item_start_position + 80 ;
						}
					}
				}
				imgutil.drawTextToLeftTop(String.valueOf(player.getInt("minionKills")), 30,font_color, 680, struct_possition+25);
				imgutil.drawTextToLeftTop(String.valueOf(Float.parseFloat(df.format((player.getInt("gold") / 1000)))) + "k", 30,font_color, 805, struct_possition+25);
				String hero = player.getString("hero");
				float hero_damage_width = (float)left_telemetry_list.getJSONObject(hero).getLong("damage") / (float)blue_damage *190*(player_list.length()/2);
				if (hero_damage_width > 190){
					hero_damage_width = 190;
				}
				float hero_healed_width = (float)left_telemetry_list.getJSONObject(hero).getLong("healed") / (float)blue_healed *190*(player_list.length()/2);
				if (hero_healed_width > 190){
					hero_healed_width = 190;
				}
				float hero_taken_width = (float)left_telemetry_list.getJSONObject(hero).getLong("taken") / (float)blue_taken *190*(player_list.length()/2);
				if (hero_taken_width > 190){
					hero_taken_width = 190;
				}
				imgutil.drawLine(30.0f,font_color,670, struct_possition+75, 670+hero_damage_width, struct_possition+75);
				imgutil.drawTextToLeftTop(String.valueOf(left_telemetry_list.getJSONObject(hero).getLong("damage")/1000)+"k", 30,font_color,670+hero_damage_width+15, struct_possition+55);
				imgutil.drawLine(30.0f,font_color,670, struct_possition+120, 670+hero_healed_width, struct_possition+120);
				imgutil.drawTextToLeftTop(String.valueOf(left_telemetry_list.getJSONObject(hero).getLong("healed")/1000)+"k", 30,font_color,670+hero_healed_width+15, struct_possition+100);
				imgutil.drawLine(30.0f,font_color,670, struct_possition+165, 670+hero_taken_width, struct_possition+165);
				imgutil.drawTextToLeftTop(String.valueOf(left_telemetry_list.getJSONObject(hero).getLong("taken")/1000)+"k", 30,font_color,670+hero_taken_width+15, struct_possition+150);
				int rankescore=0;
				if (Ranke_type != 4){
					if (Ranke_type == 1){
						rankescore= player.getInt("ranked_points");
					}else if (Ranke_type == 2){
						rankescore= player.getInt("ranked_5v5_points");
					}else if (Ranke_type == 3){
						rankescore= player.getInt("blitz_points");
					}
					imgutil.drawTextToLeftTop(String.valueOf(rankescore), 40, font_color, 930, struct_possition+130);
					String tier = Util.Match_SkyTier(rankescore).split(" ")[0];
					BufferedImage tier_bitmap = Util.get_img("mini_tier"+tier+".png");
					imgutil.createWaterMaskLeftTop(tier_bitmap, 910, struct_possition+10);
				}
				
				
				
				struct_possition+=193;
			}
			imgutil.drawwater(Util.get_img("one_roster.png"),0,struct_possition);
			imgutil.drawTextToLeftTop("上面为胜利队伍", 60,font_color, 20, struct_possition+30);
			imgutil.drawTextToLeftTop(right_team_kill, 60,font_color, 910, struct_possition+60);
			imgutil.drawTextToLeftTop(right_team_turret, 40,font_color,680, struct_possition+110);
			imgutil.drawTextToLeftTop(right_team_gold+"K", 40,font_color, 700, struct_possition+55);
			
			struct_possition+=193;
			//红方
			for(int i =0;i<rights_list.length();i+=1){
				JSONObject player = rights_list.getJSONObject(i);
				BufferedImage y  = Util.get_img("one_player.png");
				imgutil.drawwater(y,0,struct_possition);
				BufferedImage hero_bitmap = Util.get_img(player.getString("hero").toLowerCase()+".png");
				imgutil.createWaterMaskLeftTop(hero_bitmap, 35, struct_possition);
				imgutil.drawTextToLeftTop(player.getString("name"), 35,font_color, 0, struct_possition+140);
				imgutil.drawTextToLeftTop(String.valueOf(player.getInt("kills")) + "/" + String.valueOf(player.getInt("deaths")) + "/" + String.valueOf(player.getInt("assists")), 40,font_color, 170, struct_possition+90);
				JSONArray items_list = player.getJSONArray("items");
				if (items_list.length() != 0)
				{
					int item_start_position = 170;
					for (int time_1 = 0; time_1 < items_list.length(); time_1++)
					{String item = items_list.getString(time_1).replace(" ", "").replace("'", "").toLowerCase();
						if (!item.equals("healingflask") && !item.equals("visiontotem"))
						{
							BufferedImage item_bitmap = Util.get_img(item+ ".png");
							imgutil.createWaterMaskLeftTop(item_bitmap, item_start_position, struct_possition+10);
							item_start_position = item_start_position + 80 ;
						}
					}
				}
				imgutil.drawTextToLeftTop(String.valueOf(player.getInt("minionKills")), 30,font_color, 680, struct_possition+25);
				imgutil.drawTextToLeftTop(String.valueOf(Float.parseFloat(df.format((player.getInt("gold") / 1000)))) + "k", 30,font_color, 805, struct_possition+25);
				String hero = player.getString("hero");
				float hero_damage_width = (float)right_telemetry_list.getJSONObject(hero).getLong("damage") / (float)blue_damage *190*(player_list.length()/2);
				if (hero_damage_width > 190){
					hero_damage_width = 190;
				}
				float hero_healed_width = (float)right_telemetry_list.getJSONObject(hero).getLong("healed") / (float)blue_healed *190*(player_list.length()/2);
				if (hero_healed_width > 190){
					hero_healed_width = 190;
				}
				float hero_taken_width = (float)right_telemetry_list.getJSONObject(hero).getLong("taken") / (float)blue_taken *190*(player_list.length()/2);
				if (hero_taken_width > 190){
					hero_taken_width = 190;
				}
				imgutil.drawLine(30.0f,font_color,670, struct_possition+75, 670+hero_damage_width, struct_possition+75);
				imgutil.drawTextToLeftTop(String.valueOf(right_telemetry_list.getJSONObject(hero).getLong("damage")/1000)+"k", 30,font_color,670+hero_damage_width+15, struct_possition+55);
				imgutil.drawLine(30.0f,font_color,670, struct_possition+120, 670+hero_healed_width, struct_possition+120);
				imgutil.drawTextToLeftTop(String.valueOf(right_telemetry_list.getJSONObject(hero).getLong("healed")/1000)+"k", 30,font_color,670+hero_healed_width+15, struct_possition+100);
				imgutil.drawLine(30.0f,font_color,670, struct_possition+165, 670+hero_taken_width, struct_possition+165);
				imgutil.drawTextToLeftTop(String.valueOf(right_telemetry_list.getJSONObject(hero).getLong("taken")/1000)+"k", 30,font_color,670+hero_taken_width+15, struct_possition+150);
				int rankescore=0;
				if (Ranke_type != 4){
					if (Ranke_type == 1){
						rankescore= player.getInt("ranked_points");
					}else if (Ranke_type == 2){
						rankescore= player.getInt("ranked_5v5_points");
					}else if (Ranke_type == 3){
						rankescore= player.getInt("blitz_points");
					}
					imgutil.drawTextToLeftTop(String.valueOf(rankescore), 40, font_color, 930, struct_possition+130);
					String tier = Util.Match_SkyTier(rankescore).split(" ")[0];
					BufferedImage tier_bitmap = Util.get_img("mini_tier"+tier+".png");
					imgutil.createWaterMaskLeftTop(tier_bitmap, 910, struct_possition+10);
				}
				
				
				
				struct_possition+=193;
			}
			
			
			
			
			
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}



		BufferedImage createdimg =imgutil.get_img_jpg();
		String file_path = Util.save_img_jpg(createdimg);
		return file_path;
    }
	
	
	
	
	
	public static String Match_detail_img(String player_name, int page)
	{
		BufferedImage ground_bitmap = Util.get_img("ground.png");
    	BufferedImage left_player_bitmap = Util.get_img("left_player.png");
		BufferedImage left_player_me_bitmap = Util.get_img("left_player_me.png");
		BufferedImage right_player_bitmap = Util.get_img("right_player.png");
		BufferedImage right_player_me_bitmap = Util.get_img("right_player_me.png");
		ImageUtil imgutil = new ImageUtil(ground_bitmap);
		Color font_color = new Color(193, 210, 240);
		try
		{
			Class.forName("org.mariadb.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/VaingloryData","root","1721115102Qq");
			Statement stmt = conn.createStatement();  
			ResultSet rs = stmt.executeQuery("SELECT * FROM Matches where player_names like \"%," + player_name + ",%\" or player_names like \"" + player_name + ",%\" or player_names like \"%," + player_name + "\" order by game_time desc");
			Connection conn_vip = DriverManager.getConnection("jdbc:mariadb://localhost:3306/vipaccount","root","1721115102Qq");
			Statement stmt_vip = conn_vip.createStatement();  
			stmt_vip.executeUpdate("insert into players(name) values(\"" + player_name + "\");");
			stmt_vip.close();
			conn_vip.close();
			int jump =0;
			while (jump < page)
			{
				rs.next();
				jump += 1;
			}
			if (!rs.next())
			{
				return "未查询到数据,可能的原因:1.该玩家不存在,2.数据库未记录该玩,3.该页数超出最大已记录数据;已将该玩家写入立刻更新列表.";
			}
			DecimalFormat df = new DecimalFormat("0.0");		
			StringBuilder left_list = new StringBuilder("");
			StringBuilder right_list = new StringBuilder("");	
			JSONObject root = new JSONObject(rs.getString("data").replaceAll("'([A-Za-z ]*)'([A-Za-z ]*)'", "'$1$2'"));
			JSONArray root_actors = root.getJSONArray("actors");
			String gamemode = Util.match_translate(root.getString("gameMode").replace(" ", "_"));
			String createdate = new SimpleDateFormat("yyyy-MM-dd_HH:mm").format(new Date(rs.getLong("game_time"))).replaceAll("20[0-9]*-", "");	
			String duration = String.valueOf((int)root.getInt("duration") / 60);
			imgutil.drawTextToLeftBottom("日期 " + createdate, 40, font_color, 0, 10);
			imgutil.drawTextToLeftBottom(duration + "分钟" , 40, font_color, 360, 10);
			imgutil.drawTextToLeftBottom(gamemode, 40, font_color, 0, 60);
			imgutil.drawTextToRightBottom("Generated by Gwen QQ_VG_Robot", 40, font_color, 5, 60);
			imgutil.drawTextToRightBottom("Powered by Tick Tock & Guild Belove(Her)", 40, font_color, 5, 10);
			JSONArray rosters_list = root.getJSONArray("rosters");
			for (int time = 0; time < rosters_list.length(); time++)
			{
				JSONObject data = rosters_list.getJSONObject(time);
				if (data.getBoolean("won"))
				{
					imgutil.drawTextToLeftTop(String.valueOf(data.getInt("heroKills")) , 60, font_color, 860, 45);
					imgutil.drawTextToLeftTop(String.valueOf(data.getInt("turretKills")), 40, font_color, 550, 45);
					imgutil.drawTextToLeftTop(String.valueOf(data.getInt("gold") / 1000), 40, font_color, 700, 45);
				}
				else
				{
					imgutil.drawTextToRightTop(String.valueOf(data.getInt("heroKills")), 60, font_color, 860, 45);
					imgutil.drawTextToRightTop(String.valueOf(data.getInt("turretKills")), 40, font_color, 550, 45);
					imgutil.drawTextToRightTop(String.valueOf(data.getInt("gold") / 1000) + "k", 40, font_color, 700, 45);
				}
			}
			for (int time =0 ; time < root_actors.length(); time++)
			{
				JSONObject root_actors$x= root_actors.getJSONObject(time);
				if (root_actors$x.getBoolean("winner"))
				{
					left_list.append(root_actors$x + ",");
				}
				else
				{
					right_list.append(root_actors$x + ",");
				}
			}
			left_list.append("@@@@");
			right_list.append("@@@@");
			JSONArray lefts_list = new JSONArray("[" + left_list.toString().replace(",@@@@", "") + "]");
			JSONArray rights_list = new JSONArray("[" + right_list.toString().replace(",@@@@", "") + "]");
			int game_type =0;
			if (left_list.length() + right_list.length() == 6)
			{
				game_type = 1;
			}
			else
			{
				game_type = 2;
			}
			if (game_type == 2)
			{
				int data_start_position = 130;
				int struct_start_position = 125;
				int items_start_position = 200;
				int hero_start_position = 145;
				for (int time =0 ; time < lefts_list.length(); time++)
				{
					JSONObject player=lefts_list.getJSONObject(time);
					if (time == 0)
					{
						if (player.getBoolean("winner"))
						{
							imgutil.drawTextToLeftTop("胜利方", 40, font_color, 0, 45);
						}
						else
						{
							imgutil.drawTextToLeftTop("失败方", 40, font_color, 0, 45);	
						}
					}
					if (player.getString("name").equals(player_name))
					{
						imgutil.createWaterMaskLeftTop(left_player_me_bitmap, 0, struct_start_position);
					}
					else
					{
						imgutil.createWaterMaskLeftTop(left_player_bitmap, 0, struct_start_position);
					}
					imgutil.drawTextToLeftTop(String.valueOf(player.getInt("minionKills")), 40, font_color, 730, data_start_position);
					imgutil.drawTextToLeftTop(String.valueOf(Float.parseFloat(df.format((player.getInt("gold") / 1000)))) + "k", 40, font_color, 550, data_start_position);
					imgutil.drawTextToLeftTop(player.getString("name"), 40, font_color, 0, data_start_position);
					imgutil.drawTextToLeftTop(String.valueOf(player.getInt("kills")) + "/" + String.valueOf(player.getInt("deaths")) + "/" + String.valueOf(player.getInt("assists")), 40, font_color, 370, data_start_position);
					JSONArray items_list = player.getJSONArray("items");
					if (items_list.length() != 0)
					{
						int item_start_position = 250;
						for (int time_1 = 0; time_1 < items_list.length(); time_1++)
						{String item = items_list.getString(time_1).replace(" ", "").replace("'", "").toLowerCase();
							if (!item.equals("healingflask") && !item.equals("visiontotem"))
							{
							BufferedImage item_bitmap = Util.get_img(item+ ".png");
							imgutil.createWaterMaskLeftTop(item_bitmap, item_start_position, items_start_position);
							item_start_position = item_start_position + 90 ;
							}
						}
					}
					BufferedImage hero_bitmap = Util.get_img(player.getString("hero").toLowerCase() + ".png");
					imgutil.createWaterMaskLeftTop(hero_bitmap, 805, hero_start_position);

					data_start_position = data_start_position + 167;
					struct_start_position = struct_start_position + 167;
					items_start_position = items_start_position + 167;
					hero_start_position = hero_start_position + 167;
				}
				data_start_position = 130;
				struct_start_position = 125;
				items_start_position = 200;
				hero_start_position = 145;
				//右边玩家开始处理
				for (int time =0 ; time < rights_list.length(); time++)
				{
					JSONObject player= rights_list.getJSONObject(time);
					if (time == 0)
					{
						if (player.getBoolean("winner"))
						{
							imgutil.drawTextToRightTop("胜利方", 40, font_color, 0, 45);
						}
						else
						{
							imgutil.drawTextToRightTop("失败方", 40, font_color, 0, 45);	
						}
					}
					if (player.getString("name").equals(player_name))
					{
						imgutil.createWaterMaskRightTop(right_player_me_bitmap, 0, struct_start_position);
					}
					else
					{
						imgutil.createWaterMaskRightTop(right_player_bitmap, 0, struct_start_position);
					}
					imgutil.drawTextToRightTop(String.valueOf(player.getInt("minionKills")), 40, font_color, 730, data_start_position);
					imgutil.drawTextToRightTop(String.valueOf(Float.parseFloat(df.format((player.getInt("gold") / 1000)))) + "k", 40, font_color, 550, data_start_position);
					imgutil.drawTextToRightTop(player.getString("name"), 40, font_color, 5, data_start_position);
					imgutil.drawTextToRightTop(String.valueOf(player.getInt("kills")) + "/" + String.valueOf(player.getInt("deaths")) + "/" + String.valueOf(player.getInt("assists")), 40, font_color, 370, data_start_position);
					JSONArray items_list = player.getJSONArray("items");
					if (items_list.length() != 0)
					{
						int item_start_position = 250;
						for (int time_1 = 0; time_1 < items_list.length(); time_1++)
						{
							String item = items_list.getString(time_1).replace(" ", "").replace("'", "").toLowerCase();
							if (!item.equals("healingflask") && !item.equals("visiontotem"))
							{
								BufferedImage item_bitmap = Util.get_img(item+ ".png");
								imgutil.createWaterMaskRightTop(item_bitmap, item_start_position, items_start_position);

								item_start_position = item_start_position + 90 ;
							}
						}
					}
					BufferedImage hero_bitmap = Util.get_img(player.getString("hero").toLowerCase() + ".png");
					imgutil.createWaterMaskRightTop(hero_bitmap, 805, hero_start_position);

					data_start_position = data_start_position + 167;
					struct_start_position = struct_start_position + 167;
					items_start_position = items_start_position + 167;
					hero_start_position = hero_start_position + 167;
				}
			}
			conn.close();
			stmt.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		BufferedImage createdimg =imgutil.get_img_jpg();
		String file_path = Util.save_img_jpg(createdimg);
		return file_path;
    }





	public static String Match_history_img(String player_name, int page)
	{
		BufferedImage ground_bitmap = Util.get_img("ground_history.png");
    	BufferedImage win_bitmap = Util.get_img("win.png");
		BufferedImage loss_bitmap = Util.get_img("loss.png");
		BufferedImage title_bitmap = Util.get_img("title.png");
		Color font_color = new Color(193, 210, 240);
		ImageUtil imgutil = new ImageUtil(ground_bitmap);
		imgutil.createWaterMaskLeftTop(title_bitmap, 0, 10);
		DecimalFormat df = new DecimalFormat("0.0");		
		String game_mode = "";
		int offset = page * 5;
		try
		{
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/VaingloryData","root","1721115102Qq");
			Statement stmt = conn.createStatement();  
			ResultSet rs = stmt.executeQuery("SELECT * FROM Matches where player_names like \"%," + player_name + ",%\" or player_names like \"" + player_name + ",%\" or player_names like \"%," + player_name + "\" order by game_time desc");

			Connection conn_vip = DriverManager.getConnection("jdbc:mariadb://localhost:3306/vipaccount","root","1721115102Qq");
			Statement stmt_vip = conn_vip.createStatement();  
			stmt_vip.executeUpdate("insert into players(name) values(\"" + player_name + "\");");
			stmt_vip.close();
			conn_vip.close();
			int jump =0;
			while (jump < offset)
			{
				rs.next();
				jump += 1;
			}
			if (!rs.next())
			{
				return "未查询到数据,可能的原因:1.该玩家不存在,2.数据库未记录该玩,3.该页数超出最大已记录数据;已将该玩家写入立刻更新列表.";
			}
			else
			{
				int data_start_position = 130;
				int struct_start_position = 125;
				int items_start_position = 200;
				int hero_start_position = 145;
				int date_start_position = 180;
				for (int i =0;i < 5;i += 1)
				{
					String data = rs.getString("data").replaceAll("'([A-Za-z ]*)'([A-Za-z ]*)'", "'$1$2'");
					JSONObject root = new JSONObject(data);
					JSONArray root_actors = root.getJSONArray("actors");
					String gamemode = Util.match_translate(root.getString("gameMode").replace(" ", "_"));
					String duration = String.valueOf((int)root.getInt("duration") / 60);
					String createdate = new SimpleDateFormat("yyyy-MM-dd_HH:mm").format(new Date(rs.getLong("game_time"))).replaceAll("20[0-9]*-", "");
					for (int time_1 = 0;time_1 < root_actors.length();time_1++)
					{
						JSONObject root_actors$x =root_actors.getJSONObject(time_1);

						if (root_actors$x.getString("name").equals(player_name))
						{
							if (root_actors$x.getBoolean("winner"))
							{
								imgutil.createWaterMaskLeftTop(win_bitmap, 0, struct_start_position);
							}
							else
							{
								imgutil.createWaterMaskLeftTop(loss_bitmap, 0, struct_start_position);
							}
							imgutil.drawTextToLeftTop(String.valueOf(root_actors$x.getInt("minionKills")), 40, font_color, 730, data_start_position);
							imgutil.drawTextToLeftTop(String.valueOf(Float.parseFloat(df.format((root_actors$x.getInt("gold") / 1000)))) + "k", 40, font_color, 550, data_start_position);
							imgutil.drawTextToLeftTop(gamemode + " " + duration + "分钟", 40, font_color, 0, data_start_position);
							imgutil.drawTextToLeftTop(createdate , 40, font_color, 0, date_start_position);
							imgutil.drawTextToLeftTop(String.valueOf(root_actors$x.getInt("kills")) + "/" + String.valueOf(root_actors$x.getInt("deaths")) + "/" + String.valueOf(root_actors$x.getInt("assists")), 40, font_color, 370, data_start_position);
							JSONArray root_actors$x_items = root_actors$x.getJSONArray("items");
							if (root_actors$x_items.length() != 0)
							{
								int item_start_position = 250;
								for (int time_2 = 0; time_2 < root_actors$x_items.length(); time_2++)
								{
									String item = root_actors$x_items.getString(time_2).replace(" ", "").replace("'", "").toLowerCase();
									if (!item.equals("healingflask") && !item.equals("visiontotem"))
									{
										BufferedImage item_bitmap = Util.get_img(item + ".png");
										imgutil.createWaterMaskLeftTop(item_bitmap, item_start_position, items_start_position);
										item_start_position = item_start_position + 90 ;
									}

								}
							}
							BufferedImage hero_bitmap = Util.get_img(root_actors$x.getString("hero").toLowerCase() + ".png");
							imgutil.createWaterMaskLeftTop(hero_bitmap, 805, hero_start_position);
							imgutil.drawTextToRightBottom("Generated by Gwen QQ_VG_Robot", 40, font_color, 5, 60);
							imgutil.drawTextToRightBottom("Powered by Tick Tock & Guild Belove(Her)", 40, font_color, 5, 10);


						}

					}
					data_start_position = data_start_position + 167;
					date_start_position = date_start_position + 167;
					struct_start_position = struct_start_position + 167;
					items_start_position = items_start_position + 167;
					hero_start_position = hero_start_position + 167;

					if (!rs.next())
					{
						break;
					}
				}
			}
			stmt.close();  
			conn.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return e.getMessage();

		}

		BufferedImage createdimg =imgutil.get_img_jpg();
		String file_path = Util.save_img_jpg(createdimg);
		return file_path;
	}


}
