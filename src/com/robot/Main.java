package com.robot;

import com.Tick_Tock.ANDROIDQQ.sdk.*;
import java.awt.image.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;

public class Main implements Plugin
{

	private List<String> servers = Arrays.asList("cn na sg ea eu sa".split(" "));

	@Override
	public void onMessageHandler(FriendMessage p1)
	{
		// TODO: Implement this method
	}

	@Override
	public void onMessageHandler(TempolarMessage p1)
	{
		// TODO: Implement this method
	}

	@Override
	public void onMessageHandler(RequestMessage p1)
	{
		// TODO: Implement this method
	}
	
	
	@Override
	public String name()
	{
		// TODO: Implement this method
		return "虚荣机器人";
	}

	@Override
	public void onLoad(API _api)
	{
		this.api = _api;
	}

	
	private API api;

	public void log(String var0) {
		String var1 = (new SimpleDateFormat("[HH:mm:ss]")).format(new Date().getTime());
		System.out.println(var1 + " " + var0);
	}

	@Override
	public String Version() {
		return (String)null;
	}

	@Override
	public String author() {
		return (String)null;
	}

	@Override
	public void onMenu(GroupMessage qqmessage)
	{
		
	}

	
	@Override
	public void onMessageHandler(GroupMessage qqmessage)
	{
		
		boolean qqgroupuin_found = false;
		List Workinggroup_list= Util.get_database_contain( "robot", "Workinggroup", "qqgroup");
		if (Workinggroup_list.toArray().length > 0)
		{
			for (Object qqnumber_recorded : Workinggroup_list)
			{
				if (qqnumber_recorded.toString().equals(String.valueOf(qqmessage.group_uin)))
				{
					qqgroupuin_found = true;
				}
			}
		}
		staticalled(qqmessage);
		if (qqgroupuin_found)
		{
			enabled(qqmessage);
			vainglory(qqmessage);
		}
		else
		{
			disabled(qqmessage);
		}
	}
	
	
	public void staticalled(GroupMessage qqmessage){
		boolean highest_authorization = qqmessage.sender_uin ==1721115102 || qqmessage.sender_uin ==403572613;
		MessageFactory factory = new MessageFactory();
		factory.Group_uin = qqmessage.group_uin;
		factory.message_type =0;
		if(qqmessage.message.matches("修改公告 .*")){
			if(highest_authorization){
				Util.update_to_database("robot","Announce_content","announcement",qqmessage.message.replaceAll("修改公告\\s+",""),"_id = 1");
				factory.Message=  "公告已修改";
				this.api.SendGroupMessage(factory);
			}else{
				factory.Message=  "拒绝访问";
				this.api.SendGroupMessage(factory);
			}
		}
		if(qqmessage.message.equals("查看授权")){
			if(highest_authorization){
				String result ="已授权群号:\n";
				List<String> group_list= Util.get_database_contain("robot","Controller","groupuin");
				for(String groupuin : group_list){
					result += Util.ridiculous_uin(groupuin)+"\n";
				}
				factory.Message=  result;
				this.api.SendGroupMessage(factory);
			}else{
				factory.Message=  "拒绝访问";
				this.api.SendGroupMessage(factory);
			}
		}
		if(qqmessage.message.matches("查看授权\\s+.*")){
			if(highest_authorization){
				
				factory.Message=  Util.read_controllers(qqmessage.message.split("\\s+")[1]);
				this.api.SendGroupMessage(factory);
			}else{
				factory.Message=  "拒绝访问";
				this.api.SendGroupMessage(factory);
			}
		}
		if(qqmessage.message.matches("运行监控")){
			List Controler_list= Util.get_database_contain("robot","Workinggroup","qqgroup");

			if(highest_authorization){
				String result ="已开机群列表:\n";
				for(Object qquin : Controler_list){
					result += Util.ridiculous_uin(String.valueOf(qquin))+"\n";
				}
				factory.Message=  result;
				this.api.SendGroupMessage(factory);
			}else{
				factory.Message=  "拒绝访问";
				this.api.SendGroupMessage(factory);
			}
		}
		if(qqmessage.message.matches("修改公告尾巴 .*")){
			if(highest_authorization){
				Util.update_to_database("robot","Announce_end","announcement_end",qqmessage.message.replaceAll("修改公告尾巴\\s+",""),"_id = 1");
				factory.Message=  "公告尾巴已修改";
				this.api.SendGroupMessage(factory);
			}else{
				factory.Message=  "拒绝访问";
				this.api.SendGroupMessage(factory);
			}
		}
		
		if (qqmessage.message.matches("开机 .*"))
		{
			if (highest_authorization)
			{
				String groupnumber = "";
				String[] message_list = qqmessage.message.split("\\s+");
				if (message_list.length >= 2)
				{
					groupnumber = message_list[1];
				}

				boolean qquin_found = false;
				List Controler_list= Util.get_database_contain("robot", "Workinggroup", "qqgroup");

				for (Object qqnumber_recorded : Controler_list)
				{
					if (qqnumber_recorded.toString().equals(groupnumber))
					{
						qquin_found = true;
					}
				}
				if (qquin_found == false)
				{
					
					Util.add_to_database("robot","Workinggroup","qqgroup",groupnumber);
					factory.Message=  "开机成功";
					this.api.SendGroupMessage(factory);

				}
				else
				{
					factory.Message=  "开机失败:无法重复开机";
					this.api.SendGroupMessage(factory);
				}
			}
			else
			{
				factory.Message=  "拒绝访问";
				this.api.SendGroupMessage(factory);
			}

		}
		if (qqmessage.message.matches("关机 .*"))
		{
			if (highest_authorization)
			{
				String groupnumber = "";
				String[] message_list = qqmessage.message.split("\\s+");
				if (message_list.length >= 2)
				{
					groupnumber = message_list[1];
				}

				boolean qquin_found = false;
				List Controler_list= Util.get_database_contain("robot", "Workinggroup", "qqgroup");

				for (Object qqnumber_recorded : Controler_list)
				{
					if (qqnumber_recorded.toString().equals(groupnumber))
					{
						qquin_found = true;
					}
				}
				if (qquin_found == false)
				{

					
					factory.Message=  "关机失败，未开机何来关机之说";
					this.api.SendGroupMessage(factory);

				}
				else
				{
					Util.delete_from_database("robot","Workinggroup","qqgroup="+groupnumber);
					factory.Message=  "关机成功";
					this.api.SendGroupMessage(factory);
				}
			}
			else
			{
				factory.Message=  "拒绝访问";
				this.api.SendGroupMessage(factory);
			}

		}
		if (qqmessage.message.matches("添加授权 .*"))
		{
			if (highest_authorization)
			{
				factory.Message = Util.add_controler(qqmessage.message.split("\\s+")[1],qqmessage.message.split("\\s+")[2]);
				this.api.SendGroupMessage(factory);
			}
			else
			{
				factory.Message=  "拒绝访问";
				this.api.SendGroupMessage(factory);
			}

		}


		if (qqmessage.message.matches("删除授权\\s+.*"))
		{
			if (highest_authorization)
			{


				factory.Message=  Util.remove_controler(qqmessage.message.split(" ")[1],qqmessage.message.split(" ")[2]);
				this.api.SendGroupMessage(factory);

			}
			else
			{
				factory.Message=  "拒绝访问";
				this.api.SendGroupMessage(factory);
			}

		}

		if (qqmessage.message.matches("查看公告")){
			List<String> announce_content_list = Util.get_database_contain("robot","Announce_content","announcement");
			String announce_content =announce_content_list.get(0);
			List<String> announce_content_end_list = Util.get_database_contain("robot","Announce_end","announcement_end");
			String announce_content_end =announce_content_end_list.get(0);
			String message_to_send = Util.get_random_string(10) +"\n"+announce_content+"\n"+announce_content_end;
			factory.Message=  message_to_send;
			this.api.SendGroupMessage(factory);


		}
		if (qqmessage.message.matches("运行状态")){
			boolean qqgroupuin_found = false;
			List Controler_list= Util.get_database_contain( "robot", "Workinggroup", "qqgroup");
			if (Controler_list.toArray().length > 0)
			{
				for (Object qqnumber_recorded : Controler_list)
				{
					if (qqnumber_recorded.toString().equals(String.valueOf(qqmessage.group_uin)))
					{
						qqgroupuin_found = true;
					}
				}
			}
			String isrunning = "未开机";
			if (qqgroupuin_found){
				isrunning = "已开机";
			}
            SimpleDateFormat format0 = new SimpleDateFormat("MM-dd HH:mm:ss");
			String message_to_send = "机器人运行状态\n随机字符:  "+Util.get_random_string(10)+"\n当前状态:  "+isrunning+"\n启动时间:  "+format0.format(this.api.GetInitiateTime())+"\n最后自动登录时间:  "+format0.format(this.api.GetLastLoginTime());

			factory.Message=  message_to_send;
			this.api.SendGroupMessage(factory);


		}
		
	}


	public void disabled(GroupMessage qqmessage){
		
		MessageFactory factory = new MessageFactory();
		factory.Group_uin = qqmessage.group_uin;
		factory.message_type =0;
		if (qqmessage.at_list.size() != 0){
			if (qqmessage.at_list.contains(qqmessage.self_uin) )
			{
				if (qqmessage.message.matches(".*开机"))
				{
					if (Util.is_controller(qqmessage.group_uin,qqmessage.sender_uin))
					{
						Util.add_to_database("robot","Workinggroup","qqgroup",String.valueOf(qqmessage.group_uin));
						factory.Message=  "机器人已开机，发送\"菜单 "+this.name()+"\"我查看功能列表。";
				        this.api.SendGroupMessage(factory);
					}
					else
					{
						factory.Message=  "没有权限开机，如果想要开关机权限，请私聊机器人，作者有空会回复，仅限群主/管理员可以获取开关机权限";
						this.api.SendGroupMessage(factory);
					}
				}
				else
				{
					factory.Message=  "未开机，艾特我并且消息内容为开机二字即可开机";
					this.api.SendGroupMessage(factory);
				}
			}
			
		}
		
		


	}

	public void enabled(GroupMessage qqmessage){
		MessageFactory factory = new MessageFactory();
		factory.Group_uin = qqmessage.group_uin;
		factory.message_type =0;
		if (qqmessage.at_list.size() != 0){
		if (qqmessage.at_list.contains(qqmessage.self_uin) )
		{
			if(qqmessage.message.matches(".*关机")){
				
				if (Util.is_controller(qqmessage.group_uin,qqmessage.sender_uin)){
					Util.delete_from_database("robot","Workinggroup","qqgroup="+String.valueOf(qqmessage.group_uin));
					factory.Message=  "已关机";
				this.api.SendGroupMessage(factory);
				}else{
					factory.Message=  "拒绝访问";
				this.api.SendGroupMessage(factory);
				}
			}
		}
      }
	}




	public void vainglory(GroupMessage qqmessage){
		MessageFactory factory = new MessageFactory();
		factory.Group_uin = qqmessage.group_uin;
		factory.message_type =0;
		
		if (qqmessage.message.matches("段位 .*"))
		{
			String player_name = null;
			String[] message_list = qqmessage.message.split("\\s+");
			String message_to_send = "";
			int message_list_length = message_list.length;
			if (message_list_length >= 2)
			{
				player_name = message_list[1];
			}
				message_to_send=VaingloryFactory.Sky_tier(player_name, "cn");
			if (!message_to_send.endsWith(".png")&&!message_to_send.endsWith(".jpg"))
			{
					factory.Message=  "查询失败";
					this.api.SendGroupMessage(factory);
				}else{
					factory.Message = message_to_send;
					factory.message_type=2;
					this.api.SendGroupImage(factory);

				}
		}
		if (qqmessage.message.matches("详细战绩 .*"))
		{
			String[] message_list = qqmessage.message.split("\\s+");
			String player_name = null;
			String page = "0";
			boolean segment_false = false;
			int message_list_length = message_list.length;
			if (message_list_length >= 2)
			{
				player_name = message_list[1];
			}
			if (message_list_length >= 3)
			{
				page = message_list[2];
				if (Pattern.compile("[^0-9]").matcher(page).find())
				{
					factory.Message="第二个参数只可以写数字";
					this.api.SendGroupMessage(factory);
					return;
				}
			}
			if (segment_false)
			{
				
			}
			else
			{

				String message_to_send=VaingloryFactory.Match_history_img(player_name, Integer.parseInt(page));
				if (!message_to_send.endsWith(".png")&&!message_to_send.endsWith(".jpg"))
				{
					factory.Message=message_to_send;
					this.api.SendGroupMessage(factory);
				}
				else
				{
				    factory.Message = message_to_send;
					factory.message_type=2;
					this.api.SendGroupImage(factory);
				}
			}
		}
		if (qqmessage.message.matches("对局 .*"))
		{
			String[] message_list = qqmessage.message.split("\\s+");
			String player_name = null;
			String page = "0";
			boolean segment_false = false;
			int message_list_length = message_list.length;
			if (message_list_length >= 2)
			{
				player_name = message_list[1];
			}
			if (message_list_length >= 3)
			{
				page = message_list[2];
				if (Pattern.compile("[^0-9]").matcher(page).find())
				{
					segment_false = true;
				}
			}
			if (segment_false)
			{
				factory.Message="第二个参数只可以写数字";
				this.api.SendGroupMessage(factory);
				return;
			}
			else
			{


				String message_to_send = VaingloryFactory.Match_detail_img(player_name, Integer.parseInt(page));
				if (!message_to_send.endsWith(".png")&&!message_to_send.endsWith(".jpg"))
				{
					factory.Message=message_to_send;
					this.api.SendGroupMessage(factory);
				}
				else
				{
					factory.Message = message_to_send;
					factory.message_type=2;
					this.api.SendGroupImage(factory);

				}

			}
		}
		if (qqmessage.message.matches("详细对局 .*"))
		{
			String[] message_list = qqmessage.message.split("\\s+");
			String player_name = null;
			String page = "0";
			boolean segment_false = false;
			int message_list_length = message_list.length;
			if (message_list_length >= 2)
			{
				player_name = message_list[1];
			}
			if (message_list_length >= 3)
			{
				page = message_list[2];
				if (Pattern.compile("[^0-9]").matcher(page).find())
				{
					segment_false = true;
				}
			}
			if (segment_false)
			{
				factory.Message="第二个参数只可以写数字";
				this.api.SendGroupMessage(factory);
				return;
			}
			else
			{


				String message_to_send = VaingloryFactory.Match_super_detail_img(player_name, Integer.parseInt(page));
				if (!message_to_send.endsWith(".png")&&!message_to_send.endsWith(".jpg"))
				{
					factory.Message=message_to_send;
					this.api.SendGroupMessage(factory);
				}
				else
				{
					factory.Message = message_to_send;
					factory.message_type=2;
					this.api.SendGroupImage(factory);

				}

			}
		}
	}



	
	
	
	
}

