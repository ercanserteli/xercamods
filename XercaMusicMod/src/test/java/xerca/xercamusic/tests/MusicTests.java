package xerca.xercamusic.tests;

import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegisterEvent;
import xerca.xercamusic.common.Mod;

@net.neoforged.fml.common.Mod("xercamusic_tests")
public class MusicTests {
    public MusicTests(IEventBus modEventBus) {
        modEventBus.addListener(this::onRegisterTestFunctions);
    }

    private void onRegisterTestFunctions(RegisterEvent event) {
        event.register(Registries.TEST_FUNCTION, helper -> {
            MusicRecipeGameTests MusicRecipeGameTestsInstance = new MusicRecipeGameTests();
            helper.register(Mod.id("string_instruments_craft"), MusicRecipeGameTestsInstance::stringInstrumentsCraft);
            helper.register(Mod.id("percussion_instruments_craft"), MusicRecipeGameTestsInstance::percussionInstrumentsCraft);
            helper.register(Mod.id("wind_instruments_craft"), MusicRecipeGameTestsInstance::windInstrumentsCraft);
            helper.register(Mod.id("keyboard_instruments_craft"), MusicRecipeGameTestsInstance::keyboardInstrumentsCraft);
            helper.register(Mod.id("music_items_craft"), MusicRecipeGameTestsInstance::musicItemsCraft);
            MusicRegressionGameTests MusicRegressionGameTestsInstance = new MusicRegressionGameTests();
            helper.register(Mod.id("fill_array_from_nbt_sorts_and_removes_overlaps"), MusicRegressionGameTestsInstance::fillArrayFromNbtSortsAndRemovesOverlaps);
            helper.register(Mod.id("finished_temp_buffer_is_consumed_once"), MusicRegressionGameTestsInstance::finishedTempBufferIsConsumedOnce);
            helper.register(Mod.id("music_box_sheet_values_are_sanitized_on_insert"), MusicRegressionGameTestsInstance::musicBoxSheetValuesAreSanitizedOnInsert);
            helper.register(Mod.id("music_box_unknown_sheet_warning_state_is_deduplicated_and_reset"), MusicRegressionGameTestsInstance::musicBoxUnknownSheetWarningStateIsDeduplicatedAndReset);
            helper.register(Mod.id("music_spirit_spawn_data_handles_invalid_block_instrument"), MusicRegressionGameTestsInstance::musicSpiritSpawnDataHandlesInvalidBlockInstrument);
            helper.register(Mod.id("single_note_client_decode_keeps_player_id_without_level_lookup"), MusicRegressionGameTestsInstance::singleNoteClientDecodeKeepsPlayerIdWithoutLevelLookup);
            helper.register(Mod.id("triple_note_client_decode_keeps_entity_id_without_level_lookup"), MusicRegressionGameTestsInstance::tripleNoteClientDecodeKeepsEntityIdWithoutLevelLookup);
            helper.register(Mod.id("clipboard_decode_accepts_current_copy_format"), MusicRegressionGameTestsInstance::clipboardDecodeAcceptsCurrentCopyFormat);
            helper.register(Mod.id("clipboard_decode_accepts_pre_glissando_copy_format"), MusicRegressionGameTestsInstance::clipboardDecodeAcceptsPreGlissandoCopyFormat);
            helper.register(Mod.id("clipboard_decode_accepts_very_old_music_format"), MusicRegressionGameTestsInstance::clipboardDecodeAcceptsVeryOldMusicFormat);
            helper.register(Mod.id("clipboard_decode_rejects_nonsense_without_throwing"), MusicRegressionGameTestsInstance::clipboardDecodeRejectsNonsenseWithoutThrowing);
            helper.register(Mod.id("signed_sheet_export_and_import_preserve_identity_and_markers"), MusicRegressionGameTestsInstance::signedSheetExportAndImportPreserveIdentityAndMarkers);
            helper.register(Mod.id("unsigned_multipart_import_from_export_gets_fresh_identity_and_consumes_buffer"), MusicRegressionGameTestsInstance::unsignedMultipartImportFromExportGetsFreshIdentityAndConsumesBuffer);
            helper.register(Mod.id("music_box_inserts_and_ejects_music_sheet"), MusicRegressionGameTestsInstance::musicBoxInsertsAndEjectsMusicSheet);
            helper.register(Mod.id("music_box_inserts_and_ejects_instrument"), MusicRegressionGameTestsInstance::musicBoxInsertsAndEjectsInstrument);
            helper.register(Mod.id("music_box_starts_and_stops_on_redstone_pulses"), MusicRegressionGameTestsInstance::musicBoxStartsAndStopsOnRedstonePulses);
            helper.register(Mod.id("music_box_ends_playback_and_emits_timed_redstone"), MusicRegressionGameTestsInstance::musicBoxEndsPlaybackAndEmitsTimedRedstone);
            helper.register(Mod.id("music_box_does_not_start_without_sheet"), MusicRegressionGameTestsInstance::musicBoxDoesNotStartWithoutSheet);
            helper.register(Mod.id("music_box_does_not_start_without_instrument"), MusicRegressionGameTestsInstance::musicBoxDoesNotStartWithoutInstrument);
            helper.register(Mod.id("music_box_empty_slots_do_not_eject_anything"), MusicRegressionGameTestsInstance::musicBoxEmptySlotsDoNotEjectAnything);
            helper.register(Mod.id("metronome_cycles_bps_and_copies_tempo_from_sheet"), MusicRegressionGameTestsInstance::metronomeCyclesBpsAndCopiesTempoFromSheet);
            helper.register(Mod.id("metronome_powered_ticks_advance_countdown_and_reset_on_repower"), MusicRegressionGameTestsInstance::metronomePoweredTicksAdvanceCountdownAndResetOnRepower);
            helper.register(Mod.id("metronome_sheet_without_tempo_falls_back_to_cycle"), MusicRegressionGameTestsInstance::metronomeSheetWithoutTempoFallsBackToCycle);
            helper.register(Mod.id("metronome_use_with_held_item_cycles_tempo_through_vanilla_dispatch"), MusicRegressionGameTestsInstance::metronomeUseWithHeldItemCyclesTempoThroughVanillaDispatch);
            helper.register(Mod.id("piano_use_with_sheet_starts_and_stops_block_instrument_playback"), MusicRegressionGameTestsInstance::pianoUseWithSheetStartsAndStopsBlockInstrumentPlayback);
            helper.register(Mod.id("piano_use_without_sheet_does_not_start_playback"), MusicRegressionGameTestsInstance::pianoUseWithoutSheetDoesNotStartPlayback);
            helper.register(Mod.id("piano_use_with_block_instrument_opens_gui_without_placing_block"), MusicRegressionGameTestsInstance::pianoUseWithBlockInstrumentOpensGuiWithoutPlacingBlock);
            helper.register(Mod.id("signed_music_sheets_stack_to_sixteen_by_same_generation"), MusicRegressionGameTestsInstance::signedMusicSheetsStackToSixteenBySameGeneration);
            helper.register(Mod.id("legacy_music_saved_data_file_migrates_to_namespaced_storage"), MusicRegressionGameTestsInstance::legacyMusicSavedDataFileMigratesToNamespacedStorage);
            helper.register(Mod.id("piano_use_out_of_range_does_not_start_playback"), MusicRegressionGameTestsInstance::pianoUseOutOfRangeDoesNotStartPlayback);
        });
    }
}
