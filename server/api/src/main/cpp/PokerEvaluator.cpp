#include <jni.h>
#include "SevenEval.h"

extern "C" {

JNIEXPORT jint JNICALL
    Java_com_twb_pokergame_service_eval_RankEvaluator_getRank(JNIEnv *env, jclass clazz, jint i, jint j,
                                                              jint k, jint m, jint n, jint p, jint q) {
        return SevenEval::GetRank(i, j, k, m, n, p, q);
    }
}
