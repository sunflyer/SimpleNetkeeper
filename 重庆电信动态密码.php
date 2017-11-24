<?php
/**
 * Created by PhpStorm.
 * User: sunfl
 * Date: 2017/8/13
 * Time: 19:33
 */
use const DYNA_PASS_KEY;
use AES;

define('DYNA_PASS_KEY' , 'xinli_zhejiang12');
define('DYNA_PASS_KEY_DEC' , '0123456789012345');

define('DYNA_PASS_HOST' , '222.177.26.5');
define('DYNA_PASS_PORT' , 9527);

class CQDynaPass
{

    private function create_guid() {
        $charid = strtoupper(md5(uniqid(mt_rand(), true)));
        $hyphen = chr(45);// "-"
        $uuid = //chr(123)// "{"
        substr($charid, 0, 8).$hyphen
        .substr($charid, 8, 4).$hyphen
        .substr($charid,12, 4).$hyphen
        .substr($charid,16, 4).$hyphen
        .substr($charid,20,12);
        //.chr(125);// "}"
        return $uuid;
    }

    public function handleRequest(string $action): void
    {
        $phone = $_GET["phone"] ?? "";

        $origin = "TEL=$phone&SEQ=".self::create_guid();
        $aes = new AES();
        $aes->set_key(DYNA_PASS_KEY);
        $origin = $aes->encrypt($origin);


        $len = strlen($origin) / 2;

        $len = base_convert($len, 10, 16);
        while(strlen($len) < 8){
            $len = "0".$len;
        }

        $origin = hex2bin($len.$origin);
      
        $client = stream_socket_client("udp://".DYNA_PASS_HOST.':'.DYNA_PASS_PORT);
        if($client && -1 !== stream_socket_sendto($client , $origin)) {

            $read_stream = array($client);
            $write_stream = NULL;
            $except = NULL;
            if (false === stream_select($read_stream, $write_stream, $except, 5)) {
                self::end(-4 , "等待超时");
            }else{
                self::end(0 , "已发送请求，请稍后查收短信。");
            }
        }else{
            self::end(-3 , "用户请求发送失败，请稍后重试");
        }
    }
}
