package com.vcchb.zncgxt;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

class Server {
    private  String IP = "";
    private  int PORT = 0;
    private  Socket TCP = null;
    private  BufferedReader BUF  = null;
    private  boolean isConnected=false;
    Server(){

    }
    Server(String IP, int PORT) throws IOException {
        try
        {
            isConnected=true;
            TCP = new Socket(IP,PORT);
            BUF = new BufferedReader(new InputStreamReader(TCP.getInputStream()));
            Log.e("SocketService","连接成功");
        }catch (Exception e)
        {
            TCP = null;
            //return false; //连接失败
            Log.e("SocketService","连接失败"+e.toString());
        }
        this.IP = IP;
        this.PORT = PORT;
        //return true; //连接成功
    }

    //TCP连接
    public boolean connect(String IP,int PORT) throws IOException
    {
        try
        {
            isConnected=true;
            TCP = new Socket(InetAddress.getByName(IP), PORT);
            BUF = new BufferedReader(new InputStreamReader(TCP.getInputStream()));
        }catch (Exception e)
        {
            return false; //连接失败
        }
        this.IP = IP;
        this.PORT = PORT;
        return true;//连接成功
    }

    //TCP重新连接
    public boolean connect() throws IOException
    {
        try
        {
            isConnected=true;
            TCP = new Socket(InetAddress.getByName(IP), PORT);
            BUF = new BufferedReader(new InputStreamReader(TCP.getInputStream()));
        }catch (Exception e)
        {
            return false; //连接失败
        }
        return true;//连接成功
    }

    //TCP发送数据
    public boolean send(byte bys[]) throws IOException
    {
        try
        {
            OutputStream ops= TCP.getOutputStream();
            ops.write(bys);
            //  ops.close();
        }catch (SocketException e)
        {
            return false; //发送失败
            //return "Connection reset by peer"; //需要重置连接
        }
        return true;//发送成功
    }

    //TCP发送数据
    public boolean send(String bys) throws IOException
    {
        try
        {
            OutputStream ops= TCP.getOutputStream();
            ops.write(bys.getBytes());
            //  ops.close();
        }catch (SocketException e)
        {
            return false; //发送失败
            //return "Connection reset by peer"; //需要重置连接
        }
        return true;//发送成功
    }


    //TCP连接关闭
    public boolean close()throws IOException
    {
        try {
            isConnected=false;
            TCP.close();
            BUF.close();
        }catch (Exception e){
            //System.out.println("error");
            return false;
        }
        return true;
    }

    public Socket getSocket()
    {
        return TCP;
    }

    //TCP读取服务器反馈信息
    public String getServerMsg() throws IOException {

        if (BUF!=null)
        {

            StringBuilder str = new StringBuilder();
            char buf[] = new char[1024];
            BUF.read(buf);//将数据读入缓存区
            for (char i:buf)
            {
                if (i!=0)
                {
                    str.append(i);
                }else
                {
                    break;
                }
            }
            //Log.d("SocketService",str.toString());
            return str.toString();
        }
        return null;
    }

    //查看TCP是否连接状态
    public boolean isConnected() {
        return isConnected && TCP.isConnected();
    }


}
