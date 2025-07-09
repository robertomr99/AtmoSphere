import android.util.Log
import com.robertomr99.atmosphere.data.Logger

class AndroidLogger : Logger {
    override fun v(tag: String, msg: String): Int = Log.v(tag, msg)
    override fun v(tag: String, msg: String, tr: Throwable?): Int = Log.v(tag, msg, tr)

    override fun d(tag: String, msg: String): Int = Log.d(tag, msg)
    override fun d(tag: String, msg: String, tr: Throwable?): Int = Log.d(tag, msg, tr)

    override fun i(tag: String, msg: String): Int = Log.i(tag, msg)
    override fun i(tag: String, msg: String, tr: Throwable?): Int = Log.i(tag, msg, tr)

    override fun w(tag: String, msg: String): Int = Log.w(tag, msg)
    override fun w(tag: String, msg: String, tr: Throwable?): Int = Log.w(tag, msg, tr)
    override fun w(tag: String, tr: Throwable): Int = Log.w(tag, tr)  // ← Sin nullable

    override fun e(tag: String, msg: String): Int = Log.e(tag, msg)
    override fun e(tag: String, msg: String, tr: Throwable?): Int = Log.e(tag, msg, tr)

    override fun wtf(tag: String, msg: String): Int = Log.wtf(tag, msg)
    override fun wtf(tag: String, msg: String, tr: Throwable?): Int = Log.wtf(tag, msg, tr)
    override fun wtf(tag: String, tr: Throwable): Int = Log.wtf(tag, tr)  // ← Sin nullable
}