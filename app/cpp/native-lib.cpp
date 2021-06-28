#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_authentificationapi_App_getApi(
        JNIEnv* env,
        jobject /* this */) {
    std::string api = "AIzaSyDCHkDMtNV-AWJuKAggqd5VdgoDgxQZDRw";
    return env->NewStringUTF(api.c_str());
}