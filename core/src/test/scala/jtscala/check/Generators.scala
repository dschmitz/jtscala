package jtscala.check

import jtscala._

import org.scalacheck._
import Prop._
import Gen._
import Arbitrary._

object Generators {
  lazy val genPoint: Gen[Point] = 
    for {
      x <- choose(-99999999999999999999.0,99999999999999999999.0)
      y <- choose(-99999999999999999999.0,99999999999999999999.0)
    } yield Point(x, y)

  lazy val genLine:Gen[Line] =
    for {
      size <-Gen.choose(2,40)
      points <- Gen.containerOfN[Set,Point](size,genPoint)
    } yield Line(points.toList)

  // Doesn't yet deal with interior rings
  lazy val genPolygon:Gen[Polygon] =
    for {
      size <-Gen.choose(6,50)
      shareSize <- Gen.choose(3,size)
      subSize1 <- Gen.choose(3,size)
      subSize2 <- Gen.choose(3,size)
      fullSet <- Gen.containerOfN[Set,Point](size,genPoint)
      sharedSet <- Gen.pick(shareSize,fullSet).map(_.toSet)
      subSet1 <- Gen.pick(subSize1,fullSet).map(_.toSet)
      subSet2 <- Gen.pick(subSize2,fullSet).map(_.toSet)
    } yield {
      val polyOne = (subSet1 ++ sharedSet).convexHull
      val polyTwo = (subSet2 ++ sharedSet).convexHull
      polyOne | polyTwo match {
        case PolygonResult(p) => p
        case _ => sys.error("Should have resulted in a polygon.")
      }
    }

  implicit lazy val arbPoint: Arbitrary[Point] =
    Arbitrary(genPoint)

  implicit lazy val arbLine: Arbitrary[Line] =
    Arbitrary(genLine)

  implicit lazy val arbPolygon: Arbitrary[Polygon] =
    Arbitrary(genPolygon)
}
