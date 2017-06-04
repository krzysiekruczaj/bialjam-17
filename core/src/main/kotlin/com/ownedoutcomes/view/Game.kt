package com.ownedoutcomes.view

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.ownedoutcomes.*
import com.ownedoutcomes.entity.TowerFactory
import com.ownedoutcomes.view.render.GameRenderer
import ktx.actors.onClick
import ktx.actors.onKey
import ktx.app.KtxScreen
import ktx.assets.toInternalFile
import ktx.math.vec2
import ktx.scene2d.*


class Game(val stage: Stage,
           val gameController: GameController,
           val gameRenderer: GameRenderer) : KtxScreen {
  private val towerFactory = TowerFactory(gameController.world)
  private lateinit var towerTypes: KButtonTable
  private lateinit var pointsLabel: Label
  private lateinit var timeToWaveLabel: Label

  val grassArray = arrayOf("Q", "W", "E", "R", "T")
  val costArray = arrayOf("50$", "200$", "1500$", "100$", "50$")

  var currentTower = 0

  val view = table {
    setFillParent(true)
    align(Align.bottomLeft)

    // Game:
    val bottomPadding = -110f
    table {
      val tilesY = (stageHeight.toInt() / fieldHeight)
      val tilesX = (stageWidth.toInt() / fieldWidth)

      val centerX = tilesX / 2

      val fieldRadius = gameController.fieldRadius
      val dirtStart = centerX - fieldRadius
      val dirtEnd = centerX + fieldRadius
      for (y in 0..tilesY - 1) {
        for (x in 0..tilesX - 1) {
          createTileStyle(x, y, dirtStart, dirtEnd)
        }
        row()
      }

    }.cell(width = stageWidth, height = stageHeight, padBottom = bottomPadding, row = true)

    // GUI:
    towerTypes = buttonGroup(minCheckedCount = 0, maxCheckedCount = 1) {
      background("brown")
      repeat(5) { i ->
        val buttonName = "tower$i"
        imageButton(style = buttonName) {
          name = buttonName
          it.height(60f).width(70f)
            .padBottom(5f).padTop(5f).padLeft(10f).padRight(10f)
        }
        label(text = costArray[i]) {
          cell ->
          run {
            cell.expand().align(Align.topRight)
          }
        }
        label(text = grassArray[i]) {
          cell ->
          run {
            cell.expand().align(Align.bottomRight)
          }
        }
      }

      // Points:
      pointsLabel = label(text = "") {
        cell ->
        run {
          cell.expand().align(Align.topRight)
        }
      }

      timeToWaveLabel = label(text = "0") {
        cell ->
        run {
          cell.expand().align(Align.bottomRight)
        }
      }
    }
    towerTypes.cell(growX = false, height = 90f, pad = 10f)

    onKey { _: InputEvent, _: KTableWidget, c: Char ->
      run {
        println("Pressed key = [$c]")
        when (c) {
          'q' -> setTowerType(0)
          'w' -> setTowerType(1)
          'e' -> setTowerType(2)
          'r' -> setTowerType(3)
          't' -> setTowerType(4)
        }
      }
    }
    setTowerType(0)
    pack()
  }

  private fun setTowerType(id: Int) {
    currentTower = id
    println("currentTower set to $currentTower")
    val buttonGroup = towerTypes.buttonGroup
    buttonGroup.checked?.isChecked = false
    buttonGroup.buttons[id].isChecked = true
  }

  private fun KTableWidget.createTileStyle(x: Int, y: Int, dirtStart: Int, dirtEnd: Int): Unit {
    when {
      x == dirtStart && y == dirtStart -> return createImageButton("grass_nw")
      x == dirtEnd && y == dirtStart -> return createImageButton("grass_ne")
      x in dirtStart..dirtEnd && y == dirtStart -> return createImageButton("grass_n")
      x == dirtStart && y == dirtEnd -> return createImageButton("grass_sw")
      x == dirtEnd && y == dirtEnd -> return createImageButton("grass_se")
      x in dirtStart..dirtEnd && y == dirtEnd -> return createImageButton("grass_s")
      x == dirtStart && y in dirtStart..dirtEnd -> return createImageButton("grass_w")
      x == dirtEnd && y in dirtStart..dirtEnd -> return createImageButton("grass_e")
      x in (dirtStart + 4)..(dirtEnd - 4) && y in (dirtStart + 4)..(dirtEnd - 4) -> return createImage("dirt")
      x in dirtStart..dirtEnd && y in dirtStart..dirtEnd -> return createImageButton("dirt")
      else -> return createImage("grass")
    }
  }

  private fun KTableWidget.createImageButton(style: String): Unit {
    imageButton(style = style) {
      onClick { _: InputEvent, actor: KImageButton, x: Float, y: Float ->

        val canAfford = when (currentTower) {
          0 -> gameController.points > towerFactory.wallTowerCost
          1 -> gameController.points > towerFactory.fastTowerCost
          2 -> gameController.points > towerFactory.tripleShotFastTowerCost
          else -> gameController.points > towerFactory.splashTowerCost
        }

        if (canAfford) {
          val translatedX = actor.getX(Align.center) - halfScreenWidth
          val translatedY = actor.getY(Align.center) - halfScreenHeight
          val towersOnField = gameController.towers.filter { it.spawnVector.x == translatedX && it.spawnVector.y == translatedY }
          val fastTowersOnField = gameController.fastTowers.filter { it.spawnVector.x == translatedX && it.spawnVector.y == translatedY }

          if (towersOnField.count() == 0 && fastTowersOnField.count() == 0) {
            println("Creating ${gameController.towers.size} castle facades. Creating facade with id = [$currentTower]")


            println("Creating Tower at [$translatedX, $translatedY]")

            when (currentTower) {
              0 -> {
                val wallTower = towerFactory.wallTower(vec2(translatedX, translatedY))
                gameController.towers.add(wallTower)
                gameController.points -= towerFactory.wallTowerCost
              }
              1 -> {
                val fastTower = towerFactory.fastTower(vec2(translatedX, translatedY))
                gameController.fastTowers.add(fastTower)
                gameController.points -= towerFactory.fastTowerCost

              }
              2 -> {
                val fastTower = towerFactory.tripleShotFastTower(vec2(translatedX, translatedY))
                gameController.fastTowers.add(fastTower)
                gameController.points -= towerFactory.tripleShotFastTowerCost
              }
              else -> {
                val splashTower = towerFactory.splashTower(vec2(translatedX, translatedY))
                gameController.towers.add(splashTower)
                gameController.points -= towerFactory.splashTowerCost

              }
            }

            actor.isChecked = false
          } else {

            if (fastTowersOnField.isNotEmpty()) {
              val tower = fastTowersOnField[0]
              tower.life = tower.maxLife * 2
              tower.maxLife = tower.maxLife * 2
              tower.lastShotTime
            } else {
              val tower = towersOnField[0]
              tower.life = tower.maxLife * 2
              tower.maxLife = tower.maxLife * 2
            }
          }
        }


      }
    }.cell(height = fieldHeight.toFloat(), width = fieldWidth.toFloat())
  }

  private fun KTableWidget.createImage(style: String) {
    image(style) {
    }.cell(height = fieldHeight.toFloat(), width = fieldWidth.toFloat())
  }

  override fun show() {
    stage.addActor(view)
    playMusic()
    Gdx.input.inputProcessor = stage
    stage.keyboardFocus = view
  }

  fun playMusic() {
    val music = Gdx.audio.newMusic("theme.mp3".toInternalFile())
    music.setOnCompletionListener {
      playMusic()
    }
    music.play()
  }

  override fun render(delta: Float) {
    gameController.update(delta)
    stage.act(delta)
    stage.draw()
    updatePoints()
    updateTime()
    gameRenderer.render(delta)
  }

  private fun updateTime() {
    val timeToNextWave = gameController.enemiesSpawnTimeout.toInt() - gameController.lastSpawnDelta.toInt() - 1
    timeToWaveLabel.setText("To next wave: $timeToNextWave")
  }

  private fun updatePoints() {
    pointsLabel.setText("Money: ${gameController.points}$")
  }
}
