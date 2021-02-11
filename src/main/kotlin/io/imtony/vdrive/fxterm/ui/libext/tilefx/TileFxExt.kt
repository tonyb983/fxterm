@file:Suppress("unused")

package io.imtony.vdrive.fxterm.ui.libext.tilefx

import eu.hansolo.tilesfx.Section
import eu.hansolo.tilesfx.Tile
import eu.hansolo.tilesfx.TileBuilder
import eu.hansolo.tilesfx.TimeSectionBuilder
import eu.hansolo.tilesfx.addons.HappinessIndicator
import eu.hansolo.tilesfx.addons.Indicator
import eu.hansolo.tilesfx.chart.ChartData
import eu.hansolo.tilesfx.chart.RadarChartMode
import eu.hansolo.tilesfx.chart.SunburstChart
import eu.hansolo.tilesfx.chart.TilesFXSeries
import eu.hansolo.tilesfx.colors.Bright
import eu.hansolo.tilesfx.colors.ColorSkin
import eu.hansolo.tilesfx.colors.Dark
import eu.hansolo.tilesfx.events.TileEvent
import eu.hansolo.tilesfx.icons.Flag
import eu.hansolo.tilesfx.skins.BarChartItem
import eu.hansolo.tilesfx.skins.LeaderBoardItem
import eu.hansolo.tilesfx.tools.*
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.chart.XYChart
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import javafx.scene.text.TextAlignment
import kotlinx.coroutines.*
import tornadofx.attachTo
import tornadofx.booleanProperty
import tornadofx.onChange
import java.time.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import eu.hansolo.tilesfx.Tile.SkinType as TileSkin
import java.time.Duration as JavaDuration

private val RND: Random by lazy { Random(LocalDateTime.now().nano) }
private const val TILE_WIDTH = 150.0
private const val TILE_HEIGHT = 150.0
private const val noOfNodes = 0

fun EventTarget.tileBuilder(
  skinType: TileSkin,
  beforeBuild: TileBuilder<*>.() -> Unit = {},
): Tile = TileBuilder
.create()
.skinType(skinType)
.apply(beforeBuild)
.build()
.attachTo(this)

fun EventTarget.tileBuilder(
  skinType: TileSkin,
  beforeBuild: TileBuilder<*>.() -> Unit = {},
  beforeAttach: (Tile) -> Unit = {},
  afterAttach: Tile.() -> Unit = {},
): Tile = TileBuilder
  .create()
  .skinType(skinType)
  .apply(beforeBuild)
  .build()
  .attachTo(this, afterAttach, beforeAttach)

fun Node.tileBuilderWithUpdater(
  skinType: TileSkin,
  coroutineScope: CoroutineScope,
  configBuilder: TileBuilder<*>.() -> Unit = {},
  configTile: Tile.() -> Unit = {},
  configUpdater: TileUpdater.() -> Unit = {},
): Tile = TileBuilder
  .create()
  .skinType(skinType)
  .apply(configBuilder)
  .build()
  .attachTo(this, configTile) { tile ->
    this.properties["tile-updater-${tile.skinType}"] = TileUpdater(tile, coroutineScope).apply(configUpdater).also { tu ->
      tu.isRunningProperty.onChange { bool ->
        if(!bool) {
          this.properties["tile-updater-${tile.skinType}"] = Any()
        }
      }
    }
  }

interface DefaultTiles {
  fun percentageTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun clockTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun gaugeTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun sparkLineTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun areaChartTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun lineChartTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun highLowTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun timerControlTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun numberTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun textTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun plusMinusTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun sliderTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun switchTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun worldTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun timeTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun barChartTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun customTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun leaderBoardTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun mapTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun radialChartTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun donutChartTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun circularProgressTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun stockTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun gaugeSparkLineTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun radarChartTile1(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun radarChartTile2(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun smoothAreaChartTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun countryTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun characterTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun flipTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun switchSliderTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun dateTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun calendarTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun sunburstTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun matrixTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun radialPercentageTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun statusTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun barGaugeTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun imageTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun timelineTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun imageCounterTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun ledTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun countdownTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun matrixIconTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun cycleStepTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun customFlagChartTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun colorTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun turnoverTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun fluidTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun fireSmokeTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun gauge2Tile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun happinessTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

  fun radialDistributionTile(
    beforeBuild: TileBuilder<*>.() -> Unit = {},
    beforeAttach: (Tile) -> Unit = {},
    afterAttach: Tile.() -> Unit = {},
  ): Tile

}

fun EventTarget.defaultTiles() = object : DefaultTiles {
  private fun makeWeekData(chartSeries: XYChart.Series<String, Number>, yVals: Array<Int>) {
    require(yVals.size == 7) { "Week data must be size 7." }
    chartSeries.data.add(XYChart.Data("MO", yVals[0]))
    chartSeries.data.add(XYChart.Data("TU", yVals[1]))
    chartSeries.data.add(XYChart.Data("WE", yVals[2]))
    chartSeries.data.add(XYChart.Data("TH", yVals[3]))
    chartSeries.data.add(XYChart.Data("FR", yVals[4]))
    chartSeries.data.add(XYChart.Data("SA", yVals[5]))
    chartSeries.data.add(XYChart.Data("SU", yVals[6]))
  }

  // AreaChart Data
  val series1 by lazy {
    XYChart.Series<String, Number>().apply {
      this.name = "Whatever"
      makeWeekData(this, arrayOf(23, 21, 20, 22, 24, 22, 20))
    }
  }

  // LineChart Data
  val series2 by lazy {
    XYChart.Series<String, Number>().apply {
      this.name = "Inside"
      makeWeekData(this, arrayOf(8, 5, 0, 2, 4, 3, 5))
    }
  }


  val series3 by lazy {
    XYChart.Series<String, Number>().apply {
      this.name = "Outside"
      makeWeekData(this, arrayOf(8, 5, 0, 2, 4, 3, 5))
    }
  }

  // TimeControl Data
  val timeSection by lazy {
    TimeSectionBuilder.create()
      .apply {
        this.start(LocalTime.now().plusSeconds(20))
        this.stop(LocalTime.now().plusHours(1))
        this.color(Tile.GRAY)
        this.highlightColor(Tile.RED)
      }.build()
      .also {
        it.setOnTimeSectionEntered { _ -> println("Section ACTIVE") }
        it.setOnTimeSectionLeft { _ -> println("Section ACTIVE") }
      }
  }

  private fun makeItem(num: Int) = "Item $num"
  private fun randomRgb() = Color.color(Random.nextDouble(), Random.nextDouble(), Random.nextDouble())
  private fun makeRandomChartData(count: Int, valueRange: IntRange, colors: Array<Color?>? = null) = arrayOfNulls<ChartData>(count).apply {
    for (i in 0 until count) {
      this[i] = ChartData(
        makeItem(i + 1),
        Random.nextDouble(valueRange.first.toDouble(), valueRange.last.toDouble()),
        colors?.get(i) ?: randomRgb(),
      )
    }
  }

  override fun percentageTile(
    beforeBuild: TileBuilder<*>.() -> Unit,
    beforeAttach: (Tile) -> Unit,
    afterAttach: Tile.() -> Unit
  ) = TileBuilder.create()
    .skinType(TileSkin.PERCENTAGE)
    .prefSize(TILE_WIDTH, TILE_HEIGHT)
    .title("Percentage Tile")
    .unit(Helper.PERCENTAGE)
    .description("Test")
    .maxValue(60.0)
    .apply(beforeBuild)
    .build()
    .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun clockTile(
    beforeBuild: TileBuilder<*>.() -> Unit,
    beforeAttach: (Tile) -> Unit,
    afterAttach: Tile.() -> Unit
  ): Tile = TileBuilder.create()
    .skinType(TileSkin.CLOCK)
    .prefSize(TILE_WIDTH, TILE_HEIGHT)
    .title("Clock Tile")
    .text("Whatever text")
    .dateVisible(true)
    .locale(Locale.US)
    .running(true)
    .apply(beforeBuild)
    .build()
    .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun gaugeTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create()
      .skinType(TileSkin.GAUGE)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .title("Gauge Tile")
      .unit("V")
      .threshold(75.0)
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun sparkLineTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create()
      .skinType(TileSkin.SPARK_LINE)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .title("SparkLine Tile")
      .unit("mb")
      .gradientStops(
        Stop(0.0, Tile.GREEN),
        Stop(0.5, Tile.YELLOW),
        Stop(1.0, Tile.RED)
      )
      .strokeWithGradient(true)
      //.smoothing(true)
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun areaChartTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create()
      .skinType(TileSkin.SMOOTHED_CHART)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .title("SmoothedChart Tile")
      .chartType(Tile.ChartType.AREA)
      //.animated(true)
      .smoothing(true)
      .tooltipTimeout(1000.0)
      .tilesFxSeries(
        TilesFXSeries(
          series1,
          Tile.BLUE,
          LinearGradient(
            0.0, 0.0, 0.0, 1.0,
            true, CycleMethod.NO_CYCLE,
            Stop(0.0, Tile.BLUE),
            Stop(1.0, Color.TRANSPARENT)
          )
        )
      )
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun lineChartTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create()
      .skinType(TileSkin.SMOOTHED_CHART)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .title("SmoothedChart Tile")
      //.animated(true)
      .smoothing(false)
      .series(series2, series3)
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun highLowTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create()
      .skinType(TileSkin.HIGH_LOW)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .title("HighLow Tile")
      .unit("\u20AC")
      .description("Test")
      .text("Whatever text")
      .referenceValue(6.7)
      .value(8.2)
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun timerControlTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create()
      .skinType(TileSkin.TIMER_CONTROL)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .title("TimerControl Tile")
      .text("Whatever text")
      .secondsVisible(true)
      .dateVisible(true)
      .timeSections(timeSection)
      .running(true)
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun numberTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create()
      .skinType(TileSkin.NUMBER)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .title("Number Tile")
      .text("Whatever text")
      .value(13.0)
      .unit("mb")
      .description("Test")
      .textVisible(true)
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun textTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create()
      .skinType(TileSkin.TEXT)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .title("Text Tile")
      .text("Whatever text")
      .description("May the force be with you\n...always")
      .descriptionAlignment(Pos.TOP_LEFT)
      .textVisible(true)
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun plusMinusTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create()
      .skinType(TileSkin.PLUS_MINUS)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .maxValue(30.0)
      .minValue(0.0)
      .title("PlusMinus Tile")
      .text("Whatever text")
      .description("Test")
      .unit("\u00B0C")
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun sliderTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create()
      .skinType(TileSkin.SLIDER)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .title("Slider Tile")
      .text("Whatever text")
      .description("Test")
      .unit("\u00B0C")
      .barBackgroundColor(Tile.FOREGROUND)
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun switchTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create()
      .skinType(TileSkin.SWITCH)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .title("Switch Tile")
      .text("Whatever text")
      .apply(beforeBuild)
      .build()
      .apply {
        this.setOnSwitchPressed { println("Switch pressed") }
        this.setOnSwitchReleased { println("Switch released") }
      }
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun worldTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create()
      .prefSize(300.0, TILE_HEIGHT)
      .skinType(TileSkin.WORLDMAP)
      .title("WorldMap Tile")
      .text("Whatever text")
      .textVisible(false)
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun timeTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create()
      .skinType(TileSkin.TIME)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .title("Time Tile")
      .text("Whatever text")
      .duration(LocalTime.of(1, 22))
      .description("Average reply time")
      .textVisible(true)
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun barChartTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create()
      .skinType(TileSkin.BAR_CHART)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .title("BarChart Tile")
      .text("Whatever text")
      .barChartItems(
        BarChartItem("Gerrit", 47.0, Tile.BLUE).apply { this.formatString = "%.1f kWh" },
        BarChartItem("Sandra", 43.0, Tile.RED).apply { this.formatString = "%.1f kWh" },
        BarChartItem("Lilli", 12.0, Tile.GREEN).apply { this.formatString = "%.1f kWh" },
        BarChartItem("Anton", 8.0, Tile.ORANGE).apply { this.formatString = "%.1f kWh" },
      )
      .decimals(0)
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun customTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create()
      .skinType(TileSkin.CUSTOM)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .title("Custom Tile")
      .text("Whatever text")
      .graphic(Button("Click Me"))
      .roundedCorners(false)
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun leaderBoardTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create()
      .skinType(TileSkin.LEADER_BOARD)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .title("LeaderBoard Tile")
      .text("Whatever text")
      .leaderBoardItems(
        LeaderBoardItem("Gerrit", 47.0),
        LeaderBoardItem("Sandra", 43.0),
        LeaderBoardItem("Lilli", 12.0),
        LeaderBoardItem("Anton", 8.0),
      )
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun mapTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create()
      .skinType(TileSkin.MAP)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .title("Map Tile")
      .text("Some text")
      .description("Description")
      .currentLocation(Location(51.91178, 7.63379, "Home", Tile.TileColor.MAGENTA.color))
      .pointsOfInterest(
        Location(51.914405, 7.635732, "POI 1", Tile.TileColor.RED.color),
        Location(51.912529, 7.631752, "POI 2", Tile.TileColor.BLUE.color),
        Location(51.923993, 7.628906, "POI 3", Tile.TileColor.YELLOW_ORANGE.color)
      )
      .mapProvider(Tile.MapProvider.TOPO)
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun radialChartTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create()
      .skinType(TileSkin.RADIAL_CHART)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .title("RadialChart Tile")
      .text("Some text")
      .textVisible(false)
      .chartData(
        *makeRandomChartData(4, 10..24)
      )
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun donutChartTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create()
      .skinType(TileSkin.DONUT_CHART)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .title("DonutChart Tile")
      .text("Some text")
      .textVisible(false)
      .chartData(
        *makeRandomChartData(4, 10..24)
      )
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun circularProgressTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create()
      .skinType(TileSkin.CIRCULAR_PROGRESS)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .title("CircularProgress Tile")
      .text("Some text")
      .unit(Helper.PERCENTAGE)
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun stockTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create()
      .skinType(TileSkin.STOCK)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .title("Stock Tile")
      .minValue(0.0)
      .maxValue(1000.0)
      .averagingPeriod(100)
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun gaugeSparkLineTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create()
      .skinType(TileSkin.GAUGE_SPARK_LINE)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .title("GaugeSparkLine Tile")
      .animated(true)
      .textVisible(false)
      .averagingPeriod(25)
      .autoReferenceValue(true)
      .barColor(Tile.YELLOW_ORANGE)
      .barBackgroundColor(Color.rgb(255, 255, 255, 0.1))
      .sections(
        Section(0.0, 33.0, Tile.LIGHT_GREEN),
        Section(33.0, 67.0, Tile.YELLOW),
        Section(67.0, 100.0, Tile.LIGHT_RED)
      )
      .sectionsVisible(true)
      .highlightSections(true)
      .strokeWithGradient(true)
      .fixedYScale(true)
      .gradientStops(
        Stop(0.0, Tile.LIGHT_GREEN),
        Stop(0.33, Tile.LIGHT_GREEN),
        Stop(0.33, Tile.YELLOW),
        Stop(0.67, Tile.YELLOW),
        Stop(0.67, Tile.LIGHT_RED),
        Stop(1.0, Tile.LIGHT_RED)
      )
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  private fun createRadarGradient(): Array<Stop> = arrayOf(
    Stop(0.00000, Color.TRANSPARENT),
    Stop(0.00001, Color.web("#3552a0")),
    Stop(0.09090, Color.web("#456acf")),
    Stop(0.27272, Color.web("#45a1cf")),
    Stop(0.36363, Color.web("#30c8c9")),
    Stop(0.45454, Color.web("#30c9af")),
    Stop(0.50909, Color.web("#56d483")),
    Stop(0.72727, Color.web("#9adb49")),
    Stop(0.81818, Color.web("#efd750")),
    Stop(0.90909, Color.web("#ef9850")),
    Stop(1.00000, Color.web("#ef6050"))
  )

  override fun radarChartTile1(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create().skinType(TileSkin.RADAR_CHART)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .minValue(0.0)
      .maxValue(50.0)
      .title("RadarChartTileSkin Sector")
      .unit("Unit")
      .radarChartMode(RadarChartMode.SECTOR)
      .gradientStops(*createRadarGradient())
      .text("Test")
      .chartData(
        *makeRandomChartData(8, 10..24)
      )
      .tooltipText("")
      .animated(true)
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun radarChartTile2(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create().skinType(TileSkin.RADAR_CHART)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .minValue(0.0)
      .maxValue(50.0)
      .title("RadarChartTileSkin Polygon")
      .unit("Unit")
      .radarChartMode(RadarChartMode.POLYGON)
      .gradientStops(*createRadarGradient())
      .text("Test")
      .chartData(
        *makeRandomChartData(8, 10..24)
      )
      .tooltipText("")
      .animated(true)
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun smoothAreaChartTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create().skinType(TileSkin.SMOOTH_AREA_CHART)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .minValue(0.0)
      .maxValue(40.0)
      .title("SmoothAreaChart Tile")
      .unit("Unit")
      .text("Test")
      //.chartType(ChartType.LINE)
      //.dataPointsVisible(true)
      .chartData(*makeRandomChartData(4, 0..25))
      .tooltipText("")
      .animated(true)
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  private val firstRank = Rank(Ranking.FIRST, Tile.YELLOW_ORANGE)

  override fun countryTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create().skinType(TileSkin.COUNTRY)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .minValue(0.0)
      .maxValue(40.0)
      .title("Country Tile")
      .unit("Unit")
      .country(Country.DE)
      .tooltipText("")
      .animated(true)
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun characterTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create().skinType(TileSkin.CHARACTER)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .title("Character Tile")
      .titleAlignment(TextAlignment.CENTER)
      .description("G")
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun flipTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create().skinType(TileSkin.FLIP)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .characters(*Helper.TIME_0_TO_5)
      .flipTimeInMS(500)
      .flipText(" ")
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun switchSliderTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create()
      .skinType(TileSkin.SWITCH_SLIDER)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .title("SwitchSlider Tile")
      .text("Test")
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun dateTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create()
      .skinType(TileSkin.DATE)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  val now = ZonedDateTime.now()
  private val calendarData = mutableListOf(
    ChartData("Item 1", now.minusDays(1).toInstant()),
    ChartData("Item 2", now.plusDays(2).toInstant()),
    ChartData("Item 3", now.plusDays(10).toInstant()),
    ChartData("Item 4", now.plusDays(15).toInstant()),
    ChartData("Item 5", now.plusDays(15).toInstant()),
    ChartData("Item 6", now.plusDays(20).toInstant()),
    ChartData("Item 7", now.plusDays(7).toInstant()),
    ChartData("Item 8", now.minusDays(1).toInstant()),
    ChartData("Item 9", now.toInstant()),
    ChartData("Item 10", now.toInstant()),
  )

  override fun calendarTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create()
      .skinType(TileSkin.CALENDAR)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .chartData(calendarData)
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  private val tree = TreeNode(ChartData("ROOT"))
  private val first = TreeNode(ChartData("1st", 8.3, Tile.BLUE), tree)
  private val second = TreeNode(ChartData("2nd", 2.2, Tile.ORANGE), tree)
  private val third = TreeNode(ChartData("3rd", 1.4, Tile.PINK), tree)
  private val fourth = TreeNode(ChartData("4th", 1.2, Tile.LIGHT_GREEN), tree)


  private val jan = TreeNode(ChartData("Jan", 3.5), first)
  private val feb = TreeNode(ChartData("Feb", 3.1), first)
  private val mar = TreeNode(ChartData("Mar", 1.7), first)
  private val apr = TreeNode(ChartData("Apr", 1.1), second)
  private val may = TreeNode(ChartData("May", 0.8), second)
  private val jun = TreeNode(ChartData("Jun", 0.3), second)
  private val jul = TreeNode(ChartData("Jul", 0.7), third)
  private val aug = TreeNode(ChartData("Aug", 0.6), third)
  private val sep = TreeNode(ChartData("Sep", 0.1), third)
  private val oct = TreeNode(ChartData("Oct", 0.5), fourth)
  private val nov = TreeNode(ChartData("Nov", 0.4), fourth)
  private val dec = TreeNode(ChartData("Dec", 0.3), fourth)


  override fun sunburstTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create().skinType(TileSkin.SUNBURST)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .title("Sunburst Tile")
      .textVisible(false)
      .sunburstTree(tree)
      .sunburstBackgroundColor(Tile.BACKGROUND)
      .sunburstTextColor(Tile.BACKGROUND)
      .sunburstUseColorFromParent(true)
      .sunburstTextOrientation(SunburstChart.TextOrientation.TANGENT)
      .sunburstAutoTextColor(true)
      .sunburstUseChartDataTextColor(true)
      .sunburstInteractive(true)
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun matrixTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create().skinType(TileSkin.MATRIX)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .title("Matrix Tile")
      .text("Any Text")
      .textVisible(false)
      .animated(true)
      .matrixSize(8, 50)
      .chartData(
        *makeRandomChartData(8, 10..24)
      )
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun radialPercentageTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create().skinType(TileSkin.RADIAL_PERCENTAGE)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      //.backgroundColor(Color.web("#26262D"))
      .maxValue(1000.0)
      .title("RadialPercentage Tile")
      .description("Product 1")
      .textVisible(false)
      .chartData(*makeRandomChartData(3, 10..24))
      .animated(true)
      .referenceValue(100.0)
      .descriptionColor(Tile.GRAY)
      //.valueColor(Tile.BLUE)
      //.unitColor(Tile.BLUE)
      .barColor(Tile.BLUE)
      .decimals(0)
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  val leftGraphics by lazy {
    Indicator(Tile.RED).apply {
      this.isOn = true
    }
  }

  val middleGraphics by lazy {
    Indicator(Tile.YELLOW).apply {
      this.isOn = true
    }
  }

  val rightGraphics by lazy {
    Indicator(Tile.GREEN).apply {
      this.isOn = true
    }
  }

  override fun statusTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create()
      .skinType(TileSkin.STATUS)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .title("Status Tile")
      .description("Notifications")
      .leftText("CRITICAL")
      .middleText("WARNING")
      .rightText("INFORMATION")
      .leftGraphics(leftGraphics)
      .middleGraphics(middleGraphics)
      .rightGraphics(rightGraphics)
      .text("Text")
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun barGaugeTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create()
      .skinType(TileSkin.BAR_GAUGE)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .minValue(0.0)
      .maxValue(100.0)
      .startFromZero(true)
      .threshold(80.0)
      .thresholdVisible(true)
      .title("BarGauge Tile")
      .unit("F")
      .text("Whatever text")
      .gradientStops(
        Stop(0.0, Bright.BLUE),
        Stop(0.1, Bright.BLUE_GREEN),
        Stop(0.2, Bright.GREEN),
        Stop(0.3, Bright.GREEN_YELLOW),
        Stop(0.4, Bright.YELLOW),
        Stop(0.5, Bright.YELLOW_ORANGE),
        Stop(0.6, Bright.ORANGE),
        Stop(0.7, Bright.ORANGE_RED),
        Stop(0.8, Bright.RED),
        Stop(1.0, Dark.RED)
      )
      .strokeWithGradient(true)
      .animated(true)
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun imageTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create()
      .skinType(TileSkin.IMAGE)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .title("Image Tile")
      .image(Image("HanSolo.png"))
      .imageMask(Tile.ImageMask.ROUND)
      .text("Whatever text")
      .textAlignment(TextAlignment.CENTER)
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun timelineTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create()
      .skinType(TileSkin.TIMELINE)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .title("Timeline Tile")
      .unit("mg/dl")
      .minValue(0.0)
      .maxValue(350.0)
      .smoothing(true)
      .lowerThreshold(70.0)
      .lowerThresholdColor(Tile.TileColor.RED.color)
      .threshold(240.0)
      .thresholdColor(Tile.TileColor.RED.color)
      .thresholdVisible(true)
      .tickLabelColor(Helper.getColorWithOpacity(Tile.FOREGROUND, 0.5))
      .sections(
        Section(0.0, 70.0, "Low", Helper.getColorWithOpacity(Dark.RED, 0.1)),
        Section(70.0, 140.0, "Ok", Helper.getColorWithOpacity(Bright.GREEN, 0.15)),
        Section(140.0, 350.0, "High", Helper.getColorWithOpacity(Dark.RED, 0.1))
      )
      .highlightSections(true)
      .sectionsVisible(true)
      .textAlignment(TextAlignment.CENTER)
      .timePeriod(JavaDuration.ofMinutes(1))
      .maxTimePeriod(JavaDuration.ofMinutes(1))
      .timePeriodResolution(TimeUnit.SECONDS)
      .numberOfValuesForTrendCalculation(5)
      .trendVisible(true)
      .maxTimePeriod(JavaDuration.ofSeconds(60))
      .gradientStops(
        Stop(0.0, Dark.RED),
        Stop(0.15, Dark.RED),
        Stop(0.2, Bright.YELLOW_ORANGE),
        Stop(0.25, Bright.GREEN),
        Stop(0.3, Bright.GREEN),
        Stop(0.35, Bright.GREEN),
        Stop(0.45, Bright.YELLOW_ORANGE),
        Stop(0.5, Bright.ORANGE),
        Stop(0.685, Dark.RED),
        Stop(1.0, Dark.RED)
      )
      .strokeWithGradient(true)
      .averageVisible(true)
      .averagingPeriod(60)
      .timeoutMs(60000)
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun imageCounterTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create()
      .skinType(TileSkin.IMAGE_COUNTER)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .title("ImageCounter Tile")
      .text("Whatever text")
      .description("Whatever\nnumbers")
      .image(Image("HanSolo.png"))
      .imageMask(Tile.ImageMask.ROUND)
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun ledTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create()
      .skinType(TileSkin.LED)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .title("Led Tile")
      .description("Description")
      .text("Whatever text")
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun countdownTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create()
      .skinType(TileSkin.COUNTDOWN_TIMER)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .title("CountdownTimer Tile")
      .description("Description")
      .text("Text")
      .barColor(Bright.ORANGE_RED)
      .timePeriod(JavaDuration.ofSeconds(30))
      .onAlarm { System.out.println("Alarm") }
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  val matrixIcon1 = MatrixIcon().apply {
    this.fillPixels(2, 5, 1, Color.BLACK)
    this.setPixelAt(1, 2, Color.BLACK)
    this.fillPixels(2, 5, 2, Color.WHITE)
    this.setPixelAt(6, 2, Color.BLACK)
    this.setPixelAt(0, 3, Color.BLACK)
    this.fillPixels(1, 2, 3, Color.WHITE)
    this.fillPixels(3, 4, 3, Color.web("#4d79ff"))
    this.fillPixels(5, 6, 3, Color.WHITE)
    this.setPixelAt(7, 3, Color.BLACK)
    this.setPixelAt(0, 4, Color.BLACK)
    this.fillPixels(1, 2, 4, Color.WHITE)
    this.fillPixels(3, 4, 4, Color.web("#4d79ff"))
    this.fillPixels(5, 6, 4, Color.WHITE)
    this.setPixelAt(7, 4, Color.BLACK)
    this.setPixelAt(1, 5, Color.BLACK)
    this.fillPixels(2, 5, 5, Color.WHITE)
    this.setPixelAt(6, 5, Color.BLACK)
    this.fillPixels(2, 5, 6, Color.BLACK)
  }

  val matrixIcon2 = MatrixIcon().apply {
    this.fillPixels(1, 6, 2, Color.BLACK)
    this.setPixelAt(0, 3, Color.BLACK)
    this.fillPixels(1, 2, 3, Color.WHITE)
    this.fillPixels(3, 4, 3, Color.web("#4d79ff"))
    this.fillPixels(5, 6, 3, Color.WHITE)
    this.setPixelAt(7, 3, Color.BLACK)
    this.setPixelAt(0, 4, Color.BLACK)
    this.fillPixels(1, 2, 4, Color.WHITE)
    this.fillPixels(3, 4, 4, Color.web("#4d79ff"))
    this.fillPixels(5, 6, 4, Color.WHITE)
    this.setPixelAt(7, 4, Color.BLACK)
    this.setPixelAt(1, 5, Color.BLACK)
    this.fillPixels(2, 5, 5, Color.WHITE)
    this.setPixelAt(6, 5, Color.BLACK)
    this.fillPixels(2, 5, 6, Color.BLACK)
  }

  val matrixIcon3 = MatrixIcon().apply {
    this.fillPixels(0, 7, 3, Color.BLACK)
    this.setPixelAt(0, 4, Color.BLACK)
    this.fillPixels(1, 2, 4, Color.WHITE)
    this.fillPixels(3, 4, 4, Color.web("#4d79ff"))
    this.fillPixels(5, 6, 4, Color.WHITE)
    this.setPixelAt(7, 4, Color.BLACK)
    this.setPixelAt(1, 5, Color.BLACK)
    this.fillPixels(2, 5, 5, Color.WHITE)
    this.setPixelAt(6, 5, Color.BLACK)
    this.fillPixels(2, 5, 6, Color.BLACK)
  }

  val matrixIcon4 = MatrixIcon().apply {
    this.setPixelAt(0, 3, Color.BLACK)
    this.setPixelAt(7, 3, Color.BLACK)
    this.fillPixels(0, 7, 4, Color.BLACK)
    this.setPixelAt(1, 5, Color.BLACK)
    this.fillPixels(2, 5, 5, Color.WHITE)
    this.setPixelAt(6, 5, Color.BLACK)
    this.fillPixels(2, 5, 6, Color.BLACK)
  }

  val matrixIcon5 = MatrixIcon().apply {
    this.setPixelAt(0, 3, Color.BLACK)
    this.setPixelAt(7, 3, Color.BLACK)
    this.setPixelAt(0, 4, Color.BLACK)
    this.setPixelAt(7, 4, Color.BLACK)
    this.setPixelAt(1, 5, Color.BLACK)
    this.fillPixels(2, 5, 5, Color.BLACK)
    this.setPixelAt(6, 5, Color.BLACK)
    this.fillPixels(2, 5, 6, Color.BLACK)
  }

  val matrixIcon6 = MatrixIcon().apply {
    this.setPixelAt(0, 3, Color.BLACK)
    this.setPixelAt(7, 3, Color.BLACK)
    this.fillPixels(0, 7, 4, Color.BLACK)
    this.setPixelAt(1, 5, Color.BLACK)
    this.fillPixels(2, 5, 5, Color.WHITE)
    this.setPixelAt(6, 5, Color.BLACK)
    this.fillPixels(2, 5, 6, Color.BLACK)
  }

  val matrixIcon7 = MatrixIcon().apply {
    this.fillPixels(0, 7, 3, Color.BLACK)
    this.setPixelAt(0, 4, Color.BLACK)
    this.fillPixels(1, 2, 4, Color.WHITE)
    this.fillPixels(3, 4, 4, Color.web("#4d79ff"))
    this.fillPixels(5, 6, 4, Color.WHITE)
    this.setPixelAt(7, 4, Color.BLACK)
    this.setPixelAt(1, 5, Color.BLACK)
    this.fillPixels(2, 5, 5, Color.WHITE)
    this.setPixelAt(6, 5, Color.BLACK)
    this.fillPixels(2, 5, 6, Color.BLACK)
  }

  val matrixIcon8 = MatrixIcon().apply {
    this.fillPixels(1, 6, 2, Color.BLACK)
    this.setPixelAt(0, 3, Color.BLACK)
    this.fillPixels(1, 2, 3, Color.WHITE)
    this.fillPixels(3, 4, 3, Color.web("#4d79ff"))
    this.fillPixels(5, 6, 3, Color.WHITE)
    this.setPixelAt(7, 3, Color.BLACK)
    this.setPixelAt(0, 4, Color.BLACK)
    this.fillPixels(1, 2, 4, Color.WHITE)
    this.fillPixels(3, 4, 4, Color.web("#4d79ff"))
    this.fillPixels(5, 6, 4, Color.WHITE)
    this.setPixelAt(7, 4, Color.BLACK)
    this.setPixelAt(1, 5, Color.BLACK)
    this.fillPixels(2, 5, 5, Color.WHITE)
    this.setPixelAt(6, 5, Color.BLACK)
    this.fillPixels(2, 5, 6, Color.BLACK)
  }

  val matrixIcon9 = MatrixIcon().apply {
    this.fillPixels(2, 5, 1, Color.BLACK)
    this.setPixelAt(1, 2, Color.BLACK)
    this.fillPixels(2, 5, 2, Color.WHITE)
    this.setPixelAt(6, 2, Color.BLACK)
    this.setPixelAt(0, 3, Color.BLACK)
    this.fillPixels(1, 2, 3, Color.WHITE)
    this.fillPixels(3, 4, 3, Color.web("#4d79ff"))
    this.fillPixels(5, 6, 3, Color.WHITE)
    this.setPixelAt(7, 3, Color.BLACK)
    this.setPixelAt(0, 4, Color.BLACK)
    this.fillPixels(1, 2, 4, Color.WHITE)
    this.fillPixels(3, 4, 4, Color.web("#4d79ff"))
    this.fillPixels(5, 6, 4, Color.WHITE)
    this.setPixelAt(7, 4, Color.BLACK)
    this.setPixelAt(1, 5, Color.BLACK)
    this.fillPixels(2, 5, 5, Color.WHITE)
    this.setPixelAt(6, 5, Color.BLACK)
    this.fillPixels(2, 5, 6, Color.BLACK)
  }

  override fun matrixIconTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create()
      .skinType(TileSkin.MATRIX_ICON)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .title("MatrixIcon Tile")
      .matrixIcons(matrixIcon1, matrixIcon2, matrixIcon3, matrixIcon4, matrixIcon5, matrixIcon6, matrixIcon7, matrixIcon8, matrixIcon9)
      .animationDuration(50)
      .animated(true)
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun cycleStepTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create()
      .skinType(TileSkin.CYCLE_STEP)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .title("CycleStep Tile")
      .textVisible(false)
      .chartData(*makeRandomChartData(5, 10..24))
      .animated(true)
      .decimals(1)
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  val name = Label("Name").apply {
    this.textFill = Tile.FOREGROUND
    this.alignment = Pos.CENTER_LEFT
    HBox.setHgrow(this, Priority.NEVER)
  }

  val spacer = Region().apply {
    this.setPrefSize(5.0, 5.0)
    HBox.setHgrow(this, Priority.ALWAYS)
  }

  val views = Label("Cases / Deaths").apply {
    this.textFill = Tile.FOREGROUND
    this.alignment = Pos.CENTER_RIGHT
    HBox.setHgrow(this, Priority.NEVER)
  }

  val header = HBox(5.0, name, spacer, views).apply {
    this.alignment = Pos.CENTER_LEFT
    this.isFillHeight = true
  }

  private fun getCountryItem(flag: Flag, text: String, data: String): HBox {
    val imageView = ImageView(flag.getImage(22.0))
    HBox.setHgrow(imageView, Priority.NEVER)
    val name = Label(text)
    name.textFill = Tile.FOREGROUND
    name.alignment = Pos.CENTER_LEFT
    HBox.setHgrow(name, Priority.NEVER)
    val spacer = Region()
    spacer.setPrefSize(5.0, 5.0)
    HBox.setHgrow(spacer, Priority.ALWAYS)
    val views = Label(data)
    views.textFill = Tile.FOREGROUND
    views.alignment = Pos.CENTER_RIGHT
    HBox.setHgrow(views, Priority.NEVER)
    val hBox = HBox(5.0, imageView, name, spacer, views)
    hBox.alignment = Pos.CENTER_LEFT
    hBox.isFillHeight = true
    return hBox
  }

  val usa = getCountryItem(Flag.UNITED_STATES_OF_AMERICA, "USA", "1.618.757 / 96.909")
  val brazil = getCountryItem(Flag.BRAZIL, "Brazil", "363.211 / 22.666")
  val uk = getCountryItem(Flag.UNITED_KINGDOM, "UK", "259.563 / 36.793")
  val spain = getCountryItem(Flag.SPAIN, "Spain", "235.772 / 28.752")
  val italy = getCountryItem(Flag.ITALY, "Italy", "229.585 / 32.785")
  val germany = getCountryItem(Flag.GERMANY, "Germany", "178.570 / 8.257")
  val france = getCountryItem(Flag.FRANCE, "France", "142.204 / 28.315")

  val dataTable = VBox(0.0, header, usa, brazil, uk, spain, italy, germany, france).apply {
    this.isFillWidth = true
  }

  override fun customFlagChartTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create()
      .skinType(TileSkin.CUSTOM)
      .title("Custom Tile Covid-19")
      .text("Data from 26.05.2020")
      .graphic(dataTable)
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun colorTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create().skinType(TileSkin.COLOR)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .title("Color Tile")
      .description("Whatever")
      .animated(false)
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun turnoverTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create().skinType(TileSkin.TURNOVER)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .title("Turnover Tile")
      .text("Gerrit Grunwald")
      .decimals(0)
      .unit("$")
      .image(Image("HanSolo.png"))
      .animated(true)
      .checkThreshold(true)
      // .onTileEvent
      .threshold(70.0) // triggers the rotation effect
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)
      .apply {
        this.setOnTileEvent {
          if (it.eventType == TileEvent.EventType.THRESHOLD_EXCEEDED) {
            this.rank = firstRank
            this.valueColor = firstRank.color
            this.unitColor = firstRank.color
          } else if (it.eventType == TileEvent.EventType.THRESHOLD_UNDERRUN) {
            this.rank = Rank.DEFAULT
            this.valueColor = Tile.FOREGROUND
            this.unitColor = Tile.FOREGROUND
          }
        }
      }

  override fun fluidTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create().skinType(TileSkin.FLUID)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .title("Fluid Tile")
      .text("Waterlevel")
      .unit("\u0025")
      .decimals(0)
      .barColor(Tile.BLUE) // defines the fluid color, alternatively use sections or gradientstops
      .animated(true)
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  override fun fireSmokeTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create().skinType(TileSkin.FIRE_SMOKE)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .title("FireSmoke Tile")
      .text("CPU temp")
      .unit("\u00b0C")
      .threshold(40.0) // triggers the fire and smoke effect
      .decimals(0)
      .animated(true)
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  private fun createGauge2Stops(): Array<Stop> = arrayOf(
    Stop(0.00, Tile.BLUE),
    Stop(0.25, Tile.GREEN),
    Stop(0.50, Tile.YELLOW),
    Stop(0.75, Tile.ORANGE),
    Stop(1.00, Tile.RED)
  )

  override fun gauge2Tile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create()
      .skinType(TileSkin.GAUGE2)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .title("Gauge2 Tile")
      .text("Whatever")
      .unit("Unit")
      .textVisible(true)
      .value(0.0)
      .gradientStops(*createGauge2Stops())
      .strokeWithGradient(true)
      .animated(true)
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)


  private val happy = HappinessIndicator(HappinessIndicator.Happiness.HAPPY, 0.67).apply {
    HBox.setHgrow(this, Priority.ALWAYS)
  }
  private val neutral = HappinessIndicator(HappinessIndicator.Happiness.NEUTRAL, 0.25).apply {
    HBox.setHgrow(this, Priority.ALWAYS)
  }
  private val unhappy = HappinessIndicator(HappinessIndicator.Happiness.UNHAPPY, 0.08).apply {
    HBox.setHgrow(this, Priority.ALWAYS)
  }

  private val happiness = HBox(unhappy, neutral, happy).apply {
    this.isFillHeight = true
  }

  override fun happinessTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create()
      .skinType(TileSkin.CUSTOM)
      .prefSize(TILE_WIDTH, TILE_HEIGHT)
      .title("Custom Tile Happiness")
      .text("Whatever")
      .textVisible(true)
      .graphic(happiness)
      .value(0.0)
      .animated(true)
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)

  private val glucoseData = MutableList(288) {
    ChartData("", RND.nextDouble() * 300 + 50)
  }

  private fun createRadialDistStops(): Array<Stop> = arrayOf(
    Stop(0.00000, Helper.getColorWithOpacity(Color.RED, 0.1)),
    Stop(0.13750, Helper.getColorWithOpacity(Color.RED, 0.1)),
    Stop(0.15625, Helper.getColorWithOpacity(Color.web("#FA711F"), 0.1)),
    Stop(0.17500, Helper.getColorWithOpacity(ColorSkin.GREEN, 0.1)),
    Stop(0.26250, Helper.getColorWithOpacity(ColorSkin.GREEN, 0.1)),
    Stop(0.35000, Helper.getColorWithOpacity(ColorSkin.GREEN, 0.1)),
    Stop(0.35010, Helper.getColorWithOpacity(ColorSkin.YELLOW, 0.1)),
    Stop(0.45000, Helper.getColorWithOpacity(Color.web("#FA711F"), 0.1)),
    Stop(0.66250, Helper.getColorWithOpacity(Color.web("#FA711F"), 0.1)),
    Stop(0.87500, Helper.getColorWithOpacity(Color.RED, 0.1)),
    Stop(1.00000, Helper.getColorWithOpacity(Color.RED, 0.1))
  )

  override fun radialDistributionTile(beforeBuild: TileBuilder<*>.() -> Unit, beforeAttach: (Tile) -> Unit, afterAttach: Tile.() -> Unit): Tile =
    TileBuilder.create()
      .skinType(TileSkin.RADIAL_DISTRIBUTION)
      .title("RadialDistribution Tile")
      .text("Whatever")
      .description("Description")
      .minValue(0.0)
      .maxValue(400.0)
      .lowerThreshold(70.0)
      .threshold(140.0)
      .tickLabelDecimals(0)
      .decimals(0)
      .chartData(glucoseData)
      .barColor(Color.rgb(254, 1, 154))
      .gradientStops(*createRadialDistStops())
      .strokeWithGradient(true)
      .apply(beforeBuild)
      .build()
      .attachTo(this@defaultTiles, afterAttach, beforeAttach)
}

class TileUpdater(private val tile: Tile, coroutineScope: CoroutineScope) {
  private val rng: Random by lazy { Random(LocalDateTime.now().nano) }

  var isRunningProperty = booleanProperty(true)
  var isRunning: Boolean
    get() = isRunningProperty.get()
    set(value) = isRunningProperty.set(value)

  init {
    coroutineScope.launch {
      while (this.isActive && isRunning) {
        when (tile.skinType) {
          Tile.SkinType.BAR_CHART -> tile.barChartItems[rng.nextInt(3)].value = rng.nextDouble() * 80
          Tile.SkinType.GAUGE -> tile.value = rng.nextDouble() * tile.range * 1.5 + tile.minValue
          Tile.SkinType.GAUGE2 -> tile.value = rng.nextDouble() * tile.range + tile.minValue
          Tile.SkinType.HIGH_LOW -> tile.value = rng.nextDouble() * 10
          Tile.SkinType.PERCENTAGE -> tile.value = rng.nextDouble() * tile.range * 1.5 + tile.minValue
          Tile.SkinType.SPARK_LINE -> tile.value = rng.nextDouble() * tile.range * 1.5 + tile.minValue
          Tile.SkinType.LEADER_BOARD -> tile.leaderBoardItems[rng.nextInt(3)].value = rng.nextDouble() * 80
          Tile.SkinType.CIRCULAR_PROGRESS -> tile.value = rng.nextDouble() * 120
          Tile.SkinType.STOCK -> tile.value = rng.nextDouble() * 50 + 500
          Tile.SkinType.GAUGE_SPARK_LINE -> tile.value = rng.nextDouble() * 100
          Tile.SkinType.COUNTRY -> tile.value = rng.nextDouble() * 100
          Tile.SkinType.CHARACTER -> tile.description = Helper.ALPHANUMERIC[rng.nextInt(Helper.ALPHANUMERIC.size - 1)]
          Tile.SkinType.FLIP -> tile.flipText = Helper.TIME_0_TO_5[rng.nextInt(Helper.TIME_0_TO_5.size - 1)]
          Tile.SkinType.RADIAL_PERCENTAGE -> {
            tile.value = rng.nextDouble() * 50
            tile.chartData.forEach { it.value = rng.nextDouble() * 50 }
          }
          Tile.SkinType.RADIAL_CHART -> tile.chartData.forEach { it.value = rng.nextDouble() * 50 }
          Tile.SkinType.DONUT_CHART -> tile.chartData.forEach { it.value = rng.nextDouble() * 50 }
          Tile.SkinType.RADAR_CHART -> tile.chartData.forEach { it.value = rng.nextDouble() * 50 }
          Tile.SkinType.MATRIX -> tile.chartData.forEach { it.value = rng.nextDouble() * 50 }
          Tile.SkinType.CYCLE_STEP -> tile.chartData.forEach { it.value = rng.nextDouble() * 50 }
          Tile.SkinType.SMOOTH_AREA_CHART -> {
            for (i in 0 until (tile.chartData.size - 1)) {
              tile.chartData[i].value = tile.chartData[i + 1].value
            }
            tile.chartData[tile.chartData.size - 1].value = rng.nextDouble() * 25
          }
          Tile.SkinType.STATUS -> {
            if (tile.leftValue > 1000) {
              tile.leftValue = 0.0
            }
            if (tile.middleValue > 1000) {
              tile.middleValue = 0.0
            }
            if (tile.rightValue > 1000) {
              tile.rightValue = 0.0
            }
            tile.leftValue = tile.leftValue + rng.nextInt(4)
            tile.middleValue = tile.middleValue + rng.nextInt(3)
            tile.rightValue = tile.rightValue + rng.nextInt(3)
          }
          Tile.SkinType.BAR_GAUGE -> tile.value = rng.nextDouble() * 100
          Tile.SkinType.IMAGE_COUNTER -> tile.increaseValue(1.0)
          Tile.SkinType.TIMELINE -> tile.addChartData(ChartData("", rng.nextDouble() * 300 + 50, Instant.now()))
          Tile.SkinType.LED -> tile.isActive = !tile.isActive
          Tile.SkinType.COUNTDOWN_TIMER -> {
            if (!tile.isRunning) {
              tile.timePeriod = JavaDuration.ofSeconds(30)
              tile.isRunning = true
            }
          }
          Tile.SkinType.COLOR -> tile.value = rng.nextDouble() * 100
          Tile.SkinType.FLUID -> tile.value = rng.nextDouble() * 100
          Tile.SkinType.FIRE_SMOKE -> tile.value = rng.nextDouble() * 100
          Tile.SkinType.TURNOVER -> tile.value = rng.nextDouble() * 100
          else -> println("No update for ${tile.skinType}.")
        }
        delay(100)
      }
    }
  }
}
