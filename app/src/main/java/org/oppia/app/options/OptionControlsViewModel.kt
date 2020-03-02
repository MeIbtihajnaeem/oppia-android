package org.oppia.app.options

import androidx.databinding.ObservableList
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.oppia.app.fragment.FragmentScope
import org.oppia.app.model.AppLanguage
import org.oppia.app.model.AudioLanguage
import org.oppia.app.model.Profile
import org.oppia.app.model.ProfileId
import org.oppia.app.model.StoryTextSize
import org.oppia.app.viewmodel.ObservableArrayList
import org.oppia.domain.profile.ProfileManagementController
import org.oppia.util.data.AsyncResult
import org.oppia.util.logging.Logger
import javax.inject.Inject

/** [ViewModel] for [OptionsFragment]. */
@FragmentScope
class OptionControlsViewModel @Inject constructor(
  private val profileManagementController: ProfileManagementController,
  private val logger: Logger
) : ViewModel() {
  private val itemViewModelList: ObservableList<OptionsItemViewModel> = ObservableArrayList()
  private lateinit var profileId: ProfileId

//  fun processOptionsList(): ObservableList<OptionsItemViewModel> {
//    itemViewModelList.add(OptionsStoryTextViewViewModel())
//
//    itemViewModelList.add(OptionsAppLanguageViewModel())
//
//    itemViewModelList.add(OptionsAudioLanguageViewModel())
//
//    return itemViewModelList
//  }

  private val profileResultLiveData: LiveData<AsyncResult<Profile>> by lazy {
    profileManagementController.getProfile(profileId)
  }

  private val profileLiveData: LiveData<Profile> by lazy { getOptionsList() }

  private fun getOptionsList(): LiveData<Profile> {
    return Transformations.map(profileResultLiveData, ::processTopicResult)
  }

  val optionListLiveData: LiveData<ObservableList<OptionsItemViewModel>> by lazy {
    Transformations.map(profileLiveData, ::processProfileList)
  }

  fun setProfileId(profileId: ProfileId) {
    this.profileId = profileId
  }

  private fun processTopicResult(topic: AsyncResult<Profile>): Profile {
    if (topic.isFailure()) {
      logger.e("TopicPracticeFragment", "Failed to retrieve topic", topic.getErrorOrNull()!!)
    }
    return topic.getOrDefault(Profile.getDefaultInstance())
  }

  private fun processProfileList(profile: Profile): ObservableList<OptionsItemViewModel> {

    val optionsStoryTextViewViewModel =
      OptionsStoryTextViewViewModel()
    val optionsAppLanguageViewModel =
      OptionsAppLanguageViewModel()
    val optionAudioViewViewModel =
      OptionsAudioLanguageViewModel()

    optionsStoryTextViewViewModel.storyTextSize = getStoryTextSize(profile.storyTextSize)
    optionsAppLanguageViewModel.appLanguage = getAppLanguage(profile.appLanguage)
    optionAudioViewViewModel.audioLanguage = getAudioLanguage(profile.audioLanguage)

    itemViewModelList.add(optionsStoryTextViewViewModel as OptionsItemViewModel)

    itemViewModelList.add(optionsAppLanguageViewModel as OptionsItemViewModel)

    itemViewModelList.add(optionAudioViewViewModel as OptionsItemViewModel)

    return itemViewModelList
  }

  fun getStoryTextSize(storyTextSize: StoryTextSize): String {
    return when (storyTextSize) {
      StoryTextSize.SMALL_TEXT_SIZE -> "Small"
      StoryTextSize.MEDIUM_TEXT_SIZE -> "Medium"
      StoryTextSize.LARGE_TEXT_SIZE -> "Large"
      else -> "Extra Large"
    }
  }

  fun getAppLanguage(appLanguage: AppLanguage): String {
    return when (appLanguage) {
      AppLanguage.ENGLISH_APP_LANGUAGE -> "English"
      AppLanguage.HINDI_APP_LANGUAGE -> "Hindi"
      AppLanguage.FRENCH_APP_LANGUAGE -> "French"
      AppLanguage.CHINESE_APP_LANGUAGE -> "Chinese"
      else -> "English"
    }
  }

  fun getAudioLanguage(audioLanguage: AudioLanguage): String {
    return when (audioLanguage) {
      AudioLanguage.NO_AUDIO -> "No Audio"
      AudioLanguage.ENGLISH_AUDIO_LANGUAGE -> "English"
      AudioLanguage.HINDI_AUDIO_LANGUAGE -> "Hindi"
      AudioLanguage.FRENCH_AUDIO_LANGUAGE -> "French"
      AudioLanguage.CHINESE_AUDIO_LANGUAGE -> "Chinese"
      else -> "No Audio"
    }
  }
}
