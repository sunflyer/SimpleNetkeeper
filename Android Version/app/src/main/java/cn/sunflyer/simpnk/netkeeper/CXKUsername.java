package cn.sunflyer.simpnk.netkeeper;
import java.util.Date;


public class CXKUsername {
		@SuppressWarnings("unused")
		private int m_ver;				//星空的版本，V12和V18两种
		private long m_lasttimec;		//上次成功的时间处理
		private String m_username;		//原始用户名
		private String m_realusername;	//真正的用户名
		private String RADIUS;
		private String LR;
		
		public static char trans(long n){
			if(n<=127)
				return (char) n;
			else
				return (char) (n-256);
		}
		
		public static byte intToByte(long n){
			if(n<=127)
				return  (byte) n;
			else
				return (byte)(n-256);
		}
		
		public  CXKUsername(String prefix , String username,String radius){
			this.m_ver = 18;
			this.m_lasttimec = 0;
			this.m_username=username;
			this.RADIUS=radius;
			this.LR=prefix;
		}

		public CXKUsername(String username){
			this("\r\n" , username,"cqxinliradius002");
		}
		
		public long GetLastTimeC(){
			return m_lasttimec;
		}

		public String Realusername(long time){
			return Realusername(LR , time);
		}

	/**获取指定时间账号，使用Date.getTime*/
		public String Realusername(String prefix , long time){
			//System.out.println(time);
			time /= 1000;
			long m_time1c;						//时间初处理m_time1c为结果,经过时间计算出的第一次加密
			long m_time1convert;				//对时间操作后的结果，此为格式字串的原始数据
			char ss[] ={0,0,0,0};		//源数据1,对m_time1convert进行计算得到格式符源数据
			byte[] by2=new byte[4];    //md5加密参数的一部分,m_time1c的byte形式
			String m_formatsring = "";				//由m_timece算出的字符串,一般为可视字符
			String m_md5;						//对初加密(m_timec字符串表示+m_username+radius)的MD5加密
			String m_md5use;					//md5 Lower模式的前两位

			long t;
			t = time;
			t *= 0x66666667;
			t >>= 0x20; //右移32位
			t >>= 0x01; //右移1位
			m_time1c = (long) t;  //强制转换

			m_lasttimec = m_time1c;

			t = m_time1c;
			by2[3] = intToByte(t & 0xFF);
			by2[2] = intToByte((t & 0xFF00) / 0x100) ;
			by2[1] = intToByte((t & 0xFF0000) / 0x10000);
			by2[0] = intToByte((t & 0xFF000000) / 0x1000000);

			//System.out.println(by2[3]+" "+by2[2]+" "+by2[1]+" "+by2[0]);

			/**
			 * 倒置过程m_time1convert为结果
			 */
			int t0=0, t1, t2, t3;
			t0 = (int) m_time1c;
			t1 = t0;
			t2 = t0;
			t3 = t0;
			t3 = t3 << 0x10;
			t1 = t1 & 0x0FF00;
			t1 = t1 | t3;
			t3 = t0;
			t3 = t3 & 0x0FF0000;
			t2 = t2 >> 0x10;
			t3 = t3 | t2;
			t1 = t1 << 0x08;
			t3 = t3 >> 0x08;
			t1 = t1 | t3;
			m_time1convert = t1;

			//System.out.println(m_time1convert);

			/**
			 * 源数据1,对m_time1convert进行计算得到格式符源数据
			 */

			long tc=0;
			tc = m_time1convert;
			ss[3] = trans(tc & 0xFF);
			ss[2] = trans((tc & 0xFF00) / 0x100)  ;
			ss[1] = trans((tc & 0xFF0000) / 0x10000);
			ss[0] = trans((tc & 0xFF000000) / 0x1000000);

			//System.out.println(ss[3]+" "+ss[2]+ " "+ss[1]+ " "+ss[0]);
			/**
			 * 格式符初加密
			 */
			char pp[] ={0,0,0,0};
			int i = 0, j = 0, k = 0;
			for (i = 0; i < 0x20; i++){
				j = i / 0x8;
				k = 3 - (i % 0x4);
				pp[k] *= 0x2;
				if (ss[j] % 2 == 1){
					pp[k]++;
				}
				ss[j] /= 2;
			}

			/**
			 * 格式符计算,m_formatsring为结果
			 */
			char pf[] ={0,0,0,0,0,0};
			short st1,st2 ;
			st1 = (short) pp[3];
			st1 /= 0x4;
			pf[0] = trans(st1);
			st1 = (short) pp[3];
			st1 = (short) (st1 & 0x3);
			st1 *= 0x10;
			pf[1] = trans(st1);
			st2 = (short) pp[2];
			st2 /= 0x10;
			st2 = (short) (st2 | st1);
			pf[1] = trans(st2);
			st1 = (short) pp[2];
			st1 = (short) (st1 & 0x0F);
			st1 *= 0x04;
			pf[2] = trans(st1);
			st2 = (short) pp[1];
			st2 /= 0x40;
			st2 = (short) (st2 | st1);
			pf[2] = trans(st2);
			st1 = (short) pp[1];
			st1 = (short) (st1 & 0x3F);
			pf[3] = trans(st1);
			st2 = (short) pp[0];
			st2 /= 0x04;
			pf[4] = trans(st2);
			st1 = (short) pp[0];
			st1 = (short) (st1 & 0x03);
			st1 *= 0x10;
			pf[5] = trans(st1);

		/*	String arr="";
			for(int x=0;x<6;x++){
				arr+=(pf[x]+" ");
			}
			System.out.println(arr);*/

			for (int n = 0; n < 6; n++){
				pf[n] += 0x20;
				if ((pf[n]) >= 0x40){
					pf[n]++;
				}
			}

			//System.out.println("m_f"+m_formatsring);

			for (int m = 0; m < 6; m++){
				m_formatsring += pf[m];
			}

			//System.out.println("m_f"+m_formatsring);

			String strInput;
			String strtem;
			if(m_username.contains("@")){
				strtem=m_username.substring(0, m_username.indexOf("@"));
			}else{
				strtem=m_username;
			}
			strInput = strtem + RADIUS;
			byte[] temp=new byte[by2.length+strInput.getBytes().length];
			System.arraycopy(by2, 0, temp, 0, by2.length);
			System.arraycopy(strInput.getBytes(),0,temp,by2.length,strInput.getBytes().length);
			m_md5 = MD5.getMD5(temp);

			//System.out.println("m5:"+m_md5);
			m_md5use = m_md5.substring(0, 2);
			m_realusername = m_formatsring + m_md5use + m_username;
			m_realusername = prefix + m_realusername;//前面两位为回车换行0D0A,接着再是后续的

			return m_realusername;
		}

	/**获取即时账号*/
		public String Realusername(){					
			long time=(new Date()).getTime();//得到系统时间，从1970.01.01.00:00:00 开始的秒数
			return this.Realusername(time);
		}
		

}
