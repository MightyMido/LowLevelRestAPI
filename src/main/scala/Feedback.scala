class Feedback {
  final private val batch_size:Int = 4
  def get_feedBack(s:Int): Double =s match
    {
      case 1 => 0
      case 2 => -0.1
      case 3 => -0.2
      case 4 => -0.3
      case 5 => -1
  }

  def persist_feedback(RequestId:BigInt, batch_number:Int ,s:Int): String =
  {
    val response = get_feedBack(s)
    s" Response Registered with value $s for $RequestId"
  }
}
