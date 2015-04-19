// SimpNKRasDialLib.cpp : 定义 DLL 应用程序的导出函数。
//

#include "stdafx.h"

#include "cqxinli_ClickDial.h"
#include "stdio.h"
#include "raserror.h"
#include "ras.h"
#include "malloc.h"
#include "Windows.h"
#include "VersionHelpers.h"
#pragma comment(lib,"rasapi32.lib")

char* jstringTostring(JNIEnv* env, jstring jstr)
{
	char* rtn = NULL;
	jclass clsstring = env->FindClass("java/lang/String");
	jstring strencode = env->NewStringUTF("utf-8");
	jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
	jbyteArray barr = (jbyteArray)env->CallObjectMethod(jstr, mid, strencode);
	jsize alen = env->GetArrayLength(barr);
	jbyte* ba = env->GetByteArrayElements(barr, JNI_FALSE);
	if (alen > 0)
	{
		rtn = (char*)malloc(alen + 1);
		memcpy(rtn, ba, alen);
		rtn[alen] = 0;
	}
	env->ReleaseByteArrayElements(barr, ba, 0);
	return rtn;
}

jstring stoJstring(JNIEnv* env, const char* pat)
{
	jclass strClass = env->FindClass("Ljava/lang/String;");
	jmethodID ctorID = env->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");
	jbyteArray bytes = env->NewByteArray(strlen(pat));
	env->SetByteArrayRegion(bytes, 0, strlen(pat), (jbyte*)pat);
	jstring encoding = env->NewStringUTF("GB2312");
	return (jstring)env->NewObject(strClass, ctorID, bytes, encoding);
}

DWORD getRasDialLen()
{
	if (IsWindowsVistaOrGreater()){
		if (IsWindows7OrGreater()){
			if (IsWindows8OrGreater()){
				return 1068;
			}
			return 1064;
		}
		return 1060;
	}
	return 0;
}

DWORD __cdecl DialWindows(const char* lpszUsername, const char* lpszPassword, const char* lpszEntryName)
{
	

	if (RasValidateEntryNameA(NULL, lpszEntryName) != ERROR_ALREADY_EXISTS){
		printf("找不到SimpNetkeeper入口，正在尝试建立入口\n");
		RASENTRYA pRasEntry;

		memset(&pRasEntry, 0, sizeof(pRasEntry));
		DWORD pRasEntrySize = 0;
		//获取操作系统中RasEntry的大小
		RasGetEntryPropertiesA(NULL, NULL, NULL, &pRasEntrySize, NULL, NULL);

		pRasEntry.szLocalPhoneNumber[0] = '\0';
		strcpy(pRasEntry.szDeviceType, "RASDT_PPPoE");
		pRasEntry.dwFramingProtocol = RASFP_Ppp;
		pRasEntry.dwType = RASET_Broadband;
		pRasEntry.dwfOptions = RASEO_SwCompression | RASEO_RemoteDefaultGateway | RASEO_NetworkLogon ;
		pRasEntry.dwfNetProtocols = RASNP_Ip | RASNP_Ipv6;
		pRasEntry.dwEncryptionType = ET_Optional;
		pRasEntry.dwfOptions2 = RASEO2_Internet | RASEO2_IPv6RemoteDefaultGateway;
		strcpy(pRasEntry.szDeviceName, "WAN 微型端口(PPPOE)");
		pRasEntry.dwSize = pRasEntrySize;//sizeof(pRasEntry);
		printf("RasEntry结构体大小：%d\n", sizeof(pRasEntry));
		//DEBUG
		/**
		RASENTRYA pRasTest;
		pRasTest.dwSize = sizeof(pRasTest);
		DWORD pTestRes;
		DWORD pTestDword=sizeof(pRasTest);
		pTestRes = RasGetEntryPropertiesA(NULL, "ChinaNet", &pRasTest, &pTestDword, NULL, NULL);
		if (!pTestRes){
		wprintf(L"%s\n%s\n%s\n%ld\n%ld\n",pRasTest.szDeviceName,pRasTest.szDeviceType,pRasTest.szLocalPhoneNumber,pRasTest.dwFramingProtocol,pRasTest.dwType);
		}
		else{
		pTestRes = RasGetEntryPropertiesA(NULL, "ChinaNet", &pRasTest, &pTestDword, NULL, NULL);
		if (!pTestRes){
		wprintf(L"%s\n%s\n%s\n%ld\n%ld\n", pRasTest.szDeviceName, pRasTest.szDeviceName, pRasTest.szLocalPhoneNumber, pRasTest.dwFramingProtocol, pRasTest.dwType);
		}
		else{
		return pTestRes;
		}
		};

		FILE *p = fopen("data.txt", "w+");
		fprintf(p,"%s\n",pRasTest.szDeviceName);
		fprintf(p, "%s\n", pRasTest.szDeviceType);
		fprintf(p, "%ld\n", pRasTest.dwfOptions);
		fclose(p);
		*/
		//创建入口
		DWORD pRasSetEntryResult=
			RasSetEntryPropertiesA(NULL, lpszEntryName, &pRasEntry, pRasEntrySize, NULL, 0);
		printf("返回值：%lu\n", pRasSetEntryResult);
		//if (pRasSetEntryResult != ERROR_SUCCESS) return pRasSetEntryResult;
	}

	RASDIALPARAMSA pRasPara;
	printf("pRasPara:%d\n",sizeof(pRasPara));
	DWORD pRasSizeParam = 0;

	strcpy(pRasPara.szEntryName, lpszEntryName);
	strcpy(pRasPara.szCallbackNumber, "");
	strcpy(pRasPara.szPhoneNumber, "");
	strcpy(pRasPara.szDomain, "");
	strcpy(pRasPara.szUserName, lpszUsername);
	strcpy(pRasPara.szPassword, lpszPassword);
	printf("%lu\n", getRasDialLen());

	pRasPara.dwSize = getRasDialLen(); //sizeof(RASDIALPARAMSA);
	printf("pRasPara:%d\n", sizeof(pRasPara));
	HRASCONN pHrasCon = NULL;
	DWORD pDialRes = RasDialA(NULL, NULL, &pRasPara, NULL, NULL, &pHrasCon);
	if (pDialRes != 0) RasHangUpA(pHrasCon);
	return pDialRes;	
}



JNIEXPORT jlong JNICALL Java_cqxinli_ClickDial_dialRasWindows
(JNIEnv *env, jobject obj, jstring username, jstring password){
	char *pDialUser = jstringTostring(env, username);
	char *pDialPswd = jstringTostring(env, password);

	return DialWindows(pDialUser, pDialPswd, "SimpNetkeeper");
}

JNIEXPORT jstring JNICALL Java_cqxinli_ClickDial_dialRasWindowsErrorStr
(JNIEnv *env, jobject obj, jlong code){
	char res[255];
	RasGetErrorStringA((UINT)code, res, (DWORD)sizeof(res));
	return stoJstring(env, res);
}






