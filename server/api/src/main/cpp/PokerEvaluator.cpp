#include <jni.h>

// Declaration of the function from the external library
// We use C++ linkage because we are compiling the C files with g++
int evaluate_7cards(int c1, int c2, int c3, int c4, int c5, int c6, int c7);

extern "C" {

JNIEXPORT jint JNICALL
Java_com_twb_pokerapp_service_game_eval_RankEvaluator_getRankNative(JNIEnv *env, jclass clazz,
                                                              jint c1, jint c2, jint c3, jint c4, jint c5, jint c6, jint c7) {
    return evaluate_7cards(c1, c2, c3, c4, c5, c6, c7);
}

}
