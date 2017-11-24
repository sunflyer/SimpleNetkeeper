<?php
namespace control;

use function dechex;
use function str_replace;
use AES;

class OldHeart
{

    private const REP_NAME = "%name%" ,
        REP_PASS = "%pass%" ,
        REP_IP = "%ipaddr%" ,
        REP_VERSION = '%ver%' ,
        REP_MAC = '%mac%' ,
        REP_PIN = '%pinnew%';

    private static function String2Hex($string){
        $hex='';
        for ($i=0; $i < strlen($string); $i++){
            $hex .= dechex(ord($string[$i]));
        }
        return $hex;
    }


//TYPE=HEARTBEAT&USER_NAME=%name%&PASSWORD=NULL&IP=%ipaddr%&VERSION_NUMBER=%ver%&DRIVER=NULLDRV&MAC=%mac%&PIN=%pinnew%
    public static function getHeartPack(int $configId , $name , $pass , $ip , $mac , $pin){

        $hrRaw = "TYPE=HEARTBEAT&USER_NAME=%name%&PASSWORD=NULL&IP=%ipaddr%&VERSION_NUMBER=%ver%&DRIVER=NULLDRV&MAC=%mac%&PIN=%pinnew%";
        $hrRaw = str_replace(self::REP_IP , $ip , $hrRaw);
        $hrRaw = str_replace(self::REP_MAC , $mac , $hrRaw);
        $hrRaw = str_replace(self::REP_VERSION , "4.9.19.699" , $hrRaw);
        $hrRaw = str_replace(self::REP_PIN , $pin , $hrRaw);
        $hrRaw = str_replace(self::REP_NAME , $name  , $hrRaw);
        $hrRaw = str_replace(self::REP_PASS , $pass , $hrRaw);

        $key = "nk4*JLk^jk>cas*>";
        $aes = new AES();
        $aes->set_key($key);
        $ret = $aes->encrypt($hrRaw );

        if($ret){
            $head = "HR70" ;
            $head = self::String2Hex($head);
            $head = $head . "0500";

            $len = strlen($ret) / 2;
            $len = dechex($len);
            while(strlen($len) < 8){
                $len = "0${len}";
            }
            //HEXed Heartbeat Raw
            $retData = $head . $len . $ret;
            return $retData;
        }
        return null;
    }
        

}

?>
