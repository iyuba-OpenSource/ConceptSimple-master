package com.suzhou.concept.bean

/**
苏州爱语吧科技有限公司
 */
class CloseServiceEvent

class ControlVideoEvent()

class NextVideoEvent

class PreviousVideoEvent

class NextOrPreviousItemEvent(val data:ConceptItem)

class ListenPlayEvent(val play:Boolean)
class ListenPlayImageEvent(val play:Boolean)

class InServiceEvent

class OutServiceEvent

/**
 * 显示外部控制音频播放的布局
 * */
class ShowOperateEvent( val isPlaying:Boolean)

class ChangeBookEvent(val type: LanguageType)

class WordRemoveEvent

/**
 * 闯关完成
 * @param rightNum 正确数量
 * */
class OverBreakEvent(val rightNum:Int)

/**
 * 在全四册与青少版之间切换时关闭底部控制播放条
 * */
class CloseBottomEvent


/**
 * 登录成功后刷新单词
 * */
class UpdateLocalWordEvent

/**
 * 文章收藏状态改变
 * */
class UpdateSpeakListEvent

class CloseDubSpeakingEvent

class PauseServiceVideoEvent

data class StudyRankEvent(val groupResponse:GroupRankResponse)

data class ExerciseEvent(val exerciseNum:Int)

data class WordEvent(val wordRight:Int,val wordNum:Int,val bookId: Int,val index:Int)