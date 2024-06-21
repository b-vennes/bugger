package com.brandenvennes

import indigo.*
import indigo.scenes.*
import monocle.syntax.all.*
import com.brandenvennes.generated.Assets

object GameScene extends Scene[StartUpData, Model, ViewModel]:

  type SceneModel     = Model
  type SceneViewModel = ViewModel

  val name: SceneName =
    SceneName("game")

  val modelLens: Lens[Model, Model] =
    Lens.keepLatest

  val viewModelLens: Lens[ViewModel, ViewModel] =
    Lens.keepLatest

  val eventFilters: EventFilters =
    EventFilters.Permissive

  val subSystems: Set[SubSystem[Model]] =
    Set()

  def updateModel(
      context: SceneContext[StartUpData],
      model: SceneModel
  ): GlobalEvent => Outcome[SceneModel] =
    case KeyboardEvent.KeyUp(Key.LEFT_ARROW)  => Outcome(model.moveBugslyLeft)
    case KeyboardEvent.KeyUp(Key.RIGHT_ARROW) => Outcome(model.moveBugslyRight)
    case KeyboardEvent.KeyUp(Key.UP_ARROW)    => Outcome(model.moveBugslyUp)
    case FrameTick =>
      if model.checkCollision then Outcome(Model.initial(model.dice))
      else Outcome(model.tick(context.delta))
    case _ => Outcome(model)

  def updateViewModel(
      context: SceneContext[StartUpData],
      model: SceneModel,
      viewModel: SceneViewModel
  ): GlobalEvent => Outcome[SceneViewModel] =
    _ => Outcome(viewModel)

  val lanes      = 10
  val leftBuffer = 50
  val size       = 32
  val halfSize   = 16

  def present(
      context: SceneContext[StartUpData],
      model: SceneModel,
      viewModel: SceneViewModel
  ): Outcome[SceneUpdateFragment] =
    logger.debugOnce(model.toString())
    Outcome(
      SceneUpdateFragment.empty
        .addLayer(
          for
            x <- Batch.fromSeq(0 to Model.maxX)
            y <- Batch.fromSeq(0 to lanes + 3)
          yield Graphic(32, 32, Assets.assets.starsMaterial)
            .moveTo(leftBuffer + (x * 32), y * 32)
        )
        .addLayer(
          Shape.Line(
            Point(leftBuffer - 4, 0),
            Point(leftBuffer, size * (lanes + 3)),
            Stroke(2, RGBA.DarkBlue)
          ),
          Shape.Line(
            Point(leftBuffer + (11 * size) + 4, 0),
            Point(leftBuffer + (11 * size) + 4, size * (lanes + 3)),
            Stroke(2, RGBA.DarkBlue)
          )
        )
        .addLayer(
          Graphic(32, 32, Assets.assets.shipMaterial)
            .moveTo(leftBuffer + (model.bugsly.location.x * size), lanes * size)
        )
        .addLayer(Batch.fromList(model.cars).map { car =>
          val x              = leftBuffer + (car.location.x * size)
          val shiftedBack    = x + (halfSize * -car.velocity)
          val shiftedForward = x + (halfSize * car.velocity)
          val y = (lanes - (model.bugsly.location.y - car.location.y)) * size
          Graphic(32, 32, Assets.assets.meteorMaterial)
            .moveTo(
              Signal
                .Lerp(
                  Point(shiftedBack, y),
                  Point(shiftedForward, y),
                  Model.updateTime
                )
                .at(model.timeSinceUpdated)
            )
        })
        .addLayer(
          TextBox(model.bugsly.location.y.abs.toString())
            .withFontSize(Pixels(20))
            .withColor(RGBA.Yellow)
            .moveTo(Point(10, 10))
        )
    )
