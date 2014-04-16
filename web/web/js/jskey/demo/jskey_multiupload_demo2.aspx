﻿<%@Page Language="C#"%>
<%@Import Namespace="Dswork.Core.Upload" %>
<%@Import Namespace="System.Collections" %>
<%@Import Namespace="System.IO" %>
<!DOCTYPE html>
<html>
<head>
<title>文件上传</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
<%
// 可用MyRequest的GetLong("fiFile", -1)替换，此处没有引用框架
long fjFile;
try
{
	fjFile = Convert.ToInt64(Request["fjFile"]);
}
catch(Exception ex)
{
	fjFile = -1L;
}
if(fjFile > 0)
{
	String str = Request["fjFileNames"];
	Hashtable map = new Hashtable();
	foreach(String s in str.Split('|'))
	{
		try
		{
			String[] s2 = s.Split(':');
			if(s2[0].Length > 0 && s2[1].Length > 0)
			{
				map.Add(s2[0], s2[1]);
			}
		}
		catch
		{
		}
	}
	long vid = JskeyUpload.GetSessionKey(Request);
	FileInfo[] filelist = JskeyUpload.GetFile(vid, fjFile);
	if(filelist != null && filelist.Length > 0)
	{
		foreach(FileInfo f in filelist)
		{
			//JskeyUpload.GetToByte(f.FullName);//转换成byte[]
			try
			{
				if(f != null)
				{
					Response.Write("<br />" + f.Name + ": " + map[f.Name]);
					Response.Write("<br />您输入的文件已经保存到：" + f.FullName + "<br />");
				}
				else
				{
					Response.Write("<br />可能已超时，文件已被删除");
				}
			}
			catch(Exception e)
			{
				Response.Write("系统出错");
			}
		}
	}
	//JskeyUpload.DelFile(JskeyUpload.GetSessionKey(Request), fjFile);//已经把文件读取完，调用这句直接删除服务器上的临时目录，节省空间
}
%>
</body>
</html>
