#include <jni.h>
#include <string>

/**
 * written by Oke Uwechue - 08/05/2019
 */
extern "C" JNIEXPORT jobjectArray JNICALL
Java_com_mydemo_jnidemo_MainActivity_stringsFromJNI(
        JNIEnv *env,
        jobject /* this */) {

    // randomize random number generator starting point based on current timestamp (in nanonseconds for increased reliability)
    struct timespec ts;
    clock_gettime(CLOCK_MONOTONIC, &ts);

    /* using 'nano-seconds' instead of 'seconds'. Seconds does not provide sufficient granularity and could lead to duplicate successive sentences */
    srand((time_t)ts.tv_nsec);

    jobjectArray result;

    // the random dictionary words
    const char *dictionary[120]= {"just","some","new","random","words","father","delete","dust","step","spectacular","shoot","lamp","colossal","infect","cart",
            "oafish","attempt","brush","automate","defeated","absorbing","verify","blossom","weak","motion","disillusioned","deranged","savor","circumflex",
            "wrist","love","cent","debonair","hallowed","rustic","purify","tempt","belief","leap","arise","wind","answer","current","need","uttermost",
            "fulfil","shock","unaccountable","lucky","cautious","floor","ruthless","nonsense","leach","gargantuan","canopy","stamp", "cricket", "nose",
            "cringe","damp","zealous","timber","grief","mandarin","velocity","plume","tint","humidify","bellicose","serpentine","axiom","yarn","petty",
            "reef","comatose","dreary","fool","extreme","martian","coin","quantify","solo","violet","quantum","ill","nobody","peers","eclectic","quorum",
            "appreciative","fling","undermine","effervescent","violet","ire","clairvoyance","mutual","symbol","commit","life","stormy","nebula","halo","monopoly",
            "hire","wire","teeth","bubbles","kilt","opal","windy","adjective","quaternion","deflate","corner","indigo","hat","sonorous","peace"
    };

    int count = 0;
    int extent = (rand() % 5) + 2; // this will be the length of the words array (between 2 and 6)

    // container to hold list of strings to be sent back to caller
    result = (jobjectArray)env->NewObjectArray(extent, env->FindClass("java/lang/String"), env->NewStringUTF(""));

    while(count++ < extent) {
        int dataIndex = (rand() % 120);
        int outputIndex = count - 1;

        env->SetObjectArrayElement(result, outputIndex, env->NewStringUTF(dictionary[dataIndex]));
    }

    return(result);
}
