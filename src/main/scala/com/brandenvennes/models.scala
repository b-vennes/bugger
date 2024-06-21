package com.brandenvennes

import indigo.{Point, Seconds}
import monocle.syntax.all.*
import indigo.shared.dice.Dice

final case class Car(location: Point, velocity: Int):
  def tick(wrapLeft: Int, wrapRight: Int): Car =
    this
      .focus(_.location.x)
      .modify(x =>
        val nextX = velocity + x
        if nextX < wrapLeft then wrapRight
        else if nextX > wrapRight then wrapLeft
        else nextX
      )

final case class Bugsly(location: Point):
  def moveLeft(edge: Int): Bugsly =
    if location.x <= edge then this
    else this.focus(_.location.x).modify(_ - 1)

  def moveRight(edge: Int): Bugsly =
    if location.x >= edge then this
    else this.focus(_.location.x).modify(_ + 1)

object Bugsly:
  val initial: Bugsly = Bugsly(Point(4, 0))

final case class Model(
    dice: Dice,
    bugsly: Bugsly,
    cars: List[Car],
    timeSinceUpdated: Seconds
):
  import Model.*

  def moveBugslyLeft: Model =
    this.focus(_.bugsly).modify(_.moveLeft(minX))

  def moveBugslyRight: Model =
    this.focus(_.bugsly).modify(_.moveRight(maxX))

  def moveBugslyUp: Model =
    this
      .focus(_.bugsly.location.y)
      .modify(_ - 1)
      .focus(_.cars)
      .modify(
        _.filter(_.location.y < bugsly.location.y + 3)
          .appended(
            Car(Point(dice.validX, bugsly.location.y - 10), dice.velocity)
          )
      )

  def tick(delta: Seconds): Model =
    if timeSinceUpdated < updateTime then
      this
        .focus(_.timeSinceUpdated)
        .modify(_ + delta)
    else
      this
        .focus(_.cars)
        .modify(cars => cars.map(_.tick(minX, maxX)))
        .focus(_.timeSinceUpdated)
        .replace(Seconds(0))

  def checkCollision: Boolean =
    cars.exists(_.location == bugsly.location)

object Model:
  def initial(dice: Dice): Model =
    Model(
      dice,
      Bugsly.initial,
      (4 to 10).map(y => Car(Point(dice.validX, -y), dice.velocity)).toList,
      Seconds(0)
    )

  val minX       = 0
  val maxX       = 10
  val updateTime = Seconds(0.3)

  extension (dice: Dice)
    def validX: Int   = dice.roll(maxX) - 1
    def velocity: Int = if dice.rollBoolean then 1 else -1
