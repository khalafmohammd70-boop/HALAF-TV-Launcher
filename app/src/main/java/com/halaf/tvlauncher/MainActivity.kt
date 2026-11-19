
package com.halaf.tvlauncher

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.HorizontalScrollView
import android.widget.ScrollView
import android.widget.GridLayout
import android.content.pm.PackageManager

class MainActivity : Activity() {

    private lateinit var root: LinearLayout
    private lateinit var content: LinearLayout

    private val blue = Color.rgb(45, 120, 220)
    private val white = Color.WHITE
    private val darkText = Color.rgb(35, 50, 75)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(100, 170, 230)
        window.navigationBarColor = Color.rgb(235, 242, 250)

        buildHome()
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    private fun background(color: Int, radius: Int): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius).toFloat()
        }
    }

    private fun text(
        value: String,
        size: Float,
        color: Int
    ): TextView {
        return TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            gravity = Gravity.CENTER_VERTICAL
            typeface = Typeface.create(
                "sans-serif-medium",
                Typeface.NORMAL
            )
        }
    }

    private fun buildHome() {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(Color.rgb(225, 239, 255))
        }

        val menu = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            setPadding(dp(8), dp(20), dp(8), dp(12))
            background = background(
                Color.rgb(245, 249, 255), 18
            )
        }

        root.addView(
            menu,
            LinearLayout.LayoutParams(dp(76), -1)
        )

        val items = listOf(
            "⌂" to "الرئيسية",
            "▶" to "Live TV",
            "▣" to "التطبيقات",
            "▤" to "الأفلام",
            "⚙" to "الإعدادات"
        )

        items.forEachIndexed { index, item ->
            val button = TextView(this).apply {
                text = item.first
                textSize = 25f
                gravity = Gravity.CENTER
                setTextColor(blue)
                background = background(
                    Color.rgb(230, 241, 255), 14
                )
                isFocusable = true
                isClickable = true
                setOnClickListener {
                    when (index) {
                        0 -> buildHome()
                        1 -> showSection("Live TV")
                        2 -> showSection("التطبيقات")
                        3 -> showSection("الأفلام والمسلسلات")
                        4 -> showSection("الإعدادات")
                    }
                }
            }

            menu.addView(
                button,
                LinearLayout.LayoutParams(
                    dp(54), dp(54)
                ).apply {
                    bottomMargin = dp(14)
                }
            )
        }

        val scroll = ScrollView(this)

        content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(24), dp(18), dp(24), dp(25))
        }

        scroll.addView(content)

        root.addView(
            scroll,
            LinearLayout.LayoutParams(0, -1, 1f)
        )

        setContentView(root)

        addTopBar()
        addWelcome()
        addSectionTitle("تطبيقاتك")
        addApplications()
        addSectionTitle("الأفلام والمسلسلات")
        addMoviePlaceholder()
    }

    private fun addTopBar() {
        val bar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val title = text("HALAF TV", 25f, darkText)
        title.typeface = Typeface.DEFAULT_BOLD

        bar.addView(
            title,
            LinearLayout.LayoutParams(0, dp(55), 1f)
        )

        val clock = text("◷  " +
            java.text.SimpleDateFormat(
                "HH:mm",
                java.util.Locale.getDefault()
            ).format(java.util.Date()),
            17f, darkText
        )

        bar.addView(clock)

        content.addView(bar)
    }

    private fun addWelcome() {
        val banner = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(25), dp(20), dp(25), dp(20))
            background = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(
                    Color.rgb(75, 155, 235),
                    Color.rgb(135, 190, 245),
                    Color.rgb(190, 220, 250)
                )
            ).apply {
                cornerRadius = dp(22).toFloat()
            }
        }

        val heading = text(
            "مرحباً بك في HALAF TV",
            27f, white
        )

        val subtitle = text(
            "كل ما تحب مشاهدته في مكان واحد",
            16f, white
        )

        banner.addView(heading)
        banner.addView(subtitle)

        content.addView(
            banner,
            LinearLayout.LayoutParams(
                -1, dp(155)
            ).apply {
                topMargin = dp(18)
                bottomMargin = dp(22)
            }
        )
    }

    private fun addSectionTitle(value: String) {
        val title = text(value, 21f, darkText)

        content.addView(
            title,
            LinearLayout.LayoutParams(
                -1, dp(48)
            )
        )
    }

    private fun addApplications() {
        val apps = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val installed = packageManager.queryIntentActivities(
            intent, PackageManager.MATCH_ALL
        )

        installed
            .filter {
                it.activityInfo.packageName != packageName
            }
            .sortedBy {
                it.loadLabel(packageManager).toString()
            }
            .take(15)
            .forEach { info ->

                val app = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    gravity = Gravity.CENTER
                    setPadding(dp(12), dp(12), dp(12), dp(12))
                    background = background(white, 18)
                    isFocusable = true
                    isClickable = true

                    setOnClickListener {
                        val launch = packageManager
                            .getLaunchIntentForPackage(
                                info.activityInfo.packageName
                            )

                        if (launch != null) startActivity(launch)
                    }
                }

                val icon = android.widget.ImageView(this).apply {
                    setImageDrawable(
                        info.loadIcon(packageManager)
                    )
                    scaleType =
                        android.widget.ImageView.ScaleType.FIT_CENTER
                }

                app.addView(icon, LinearLayout.LayoutParams(
                    dp(55), dp(55)
                ))

                val name = text(
                    info.loadLabel(packageManager).toString(),
                    13f, darkText
                )

                name.gravity = Gravity.CENTER

                app.addView(name)

                apps.addView(
                    app,
                    LinearLayout.LayoutParams(
                        dp(125), dp(115)
                    ).apply {
                        marginEnd = dp(12)
                    }
                )
            }

        val horizontal = HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            addView(apps)
        }

        content.addView(horizontal)
    }

    private fun addMoviePlaceholder() {
        val card = TextView(this).apply {
            text = "🎬     مكتبة الأفلام والمسلسلات\n\nستظهر هنا مكتبتك السينمائية"
            textSize = 18f
            setTextColor(darkText)
            gravity = Gravity.CENTER
            background = background(white, 20)
            isFocusable = true
        }

        content.addView(
            card,
            LinearLayout.LayoutParams(
                -1, dp(145)
            ).apply {
                bottomMargin = dp(20)
            }
        )
    }

    private fun showSection(title: String) {
        content.removeAllViews()

        val heading = text(title, 28f, darkText)

        content.addView(
            heading,
            LinearLayout.LayoutParams(-1, dp(100))
        )

        val message = text(
            "سيتم تطوير هذا القسم في المراحل القادمة",
            18f, darkText
        )

        content.addView(message)
    }
}
