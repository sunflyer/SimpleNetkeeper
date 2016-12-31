using System;

namespace SimpleNetkeeper_Tool {

  ///
  ///  转换Netkeeper账号PIN码中时间字符为实际时间戳
  ///  例如，加密后的账号为 \r1 ABCDEF ea 12345678@cqit
  ///  其中 \r1 为固定前缀 ， ABCDEF为PIN码的时间戳（即使其中任意字符为空格） ， ea为PIN码的MD5加密部分，后面的12345678@cqit为你本身的账号
  ///  用法 getTime("ABCDEF")
  ///  返回 时间戳的Int形式
  ///
  ///   此源代码基于GPL协议公开，请不要违反协议。虽然我知道多数中国人的劣根性本就如此。
  ///   By  CrazyChen , License under GPL
  class NKAcc {
        // 时间戳转为C#格式时间
        private static DateTime StampToDateTime(string timeStamp)
        {
            DateTime dateTimeStart = TimeZone.CurrentTimeZone.ToLocalTime(new DateTime(1970, 1, 1));
            long lTime = long.Parse(timeStamp + "0000000");
            TimeSpan toNow = new TimeSpan(lTime);

            return dateTimeStart.Add(toNow);
        }

        public static int toTime(byte[] x) {
            if (BitConverter.IsLittleEndian)
                Array.Reverse(x);

            return BitConverter.ToInt32(x, 0);
        }

        public static int getTime(string pinCode) {
            byte[] pin = Encoding.UTF8.GetBytes(pinCode);

            byte[] newpin = new byte[pin.Length];

            for (int i = 0; i < 6; i++)
            {
                if (pin[i] > 0x40)
                {
                    newpin[i] = (byte)(pin[i] - 0x21);
                }
                else
                {
                    newpin[i] = (byte)(pin[i] - 0x20);
                }

            }
            //Console.Write(BitConverter.ToString(newpin).Replace("-", " "));

            byte[] timeHash = new byte[4];

            //hash 0 高6
            timeHash[0] = (byte)(newpin[0] << 2);
            timeHash[0] = (byte)(((newpin[1] >> 4) & 0x3) | timeHash[0]);


            //hash 3 高6
            timeHash[3] = (byte)(newpin[4] << 2); //高6
            timeHash[3] = (byte)(timeHash[3] | (byte)(newpin[5] >> 4)); //低2

            //hash 2 低6
            timeHash[2] = newpin[3]; //低6
            timeHash[2] = (byte)(((newpin[2] & 0x3) << 6) | timeHash[2]); //高2

            //hash 1
            timeHash[1] = (byte)((newpin[2] >> 2) & 0xF); //低4
            timeHash[1] = (byte)(((newpin[1] & 0xF) << 4) | timeHash[1]); //高4

            int[] temp = new int[32];
            for (int i = 0; i < 4; i++)
            {
                temp[i] = (timeHash[i] & 128) == 0 ? 0 : 1;
                temp[4 + i] = (timeHash[i] & 64) == 0 ? 0 : 1;
                temp[8 + i] = (timeHash[i] & 32) == 0 ? 0 : 1;
                temp[12 + i] = (timeHash[i] & 16) == 0 ? 0 : 1;
                temp[16 + i] = (timeHash[i] & 8) == 0 ? 0 : 1;
                temp[20 + i] = (timeHash[i] & 4) == 0 ? 0 : 1;
                temp[24 + i] = (timeHash[i] & 2) == 0 ? 0 : 1;
                temp[28 + i] = (timeHash[i] & 1) == 0 ? 0 : 1;
            }

            /*
            for (int i = 0; i < 32; i++)
            {
                Console.Write(temp[i] + " ");
            }
            Console.WriteLine();
            */
            byte[] timestr = new byte[4];
            for (int i = 0; i < 4; i++)
            {
                for (int j = 0; j < 8; j++)
                {
                    timestr[3 - i] += (byte)(temp[i * 8 + j] * Math.Pow(2, j));
                }
            }

            return toTime(timestr);
        }    
  }
}
