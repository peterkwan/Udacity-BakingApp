package org.peterkwan.udacity.bakingapp.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Guideline;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.ErrorMessageProvider;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import org.peterkwan.udacity.bakingapp.R;
import org.peterkwan.udacity.bakingapp.data.entity.RecipeStep;
import org.peterkwan.udacity.bakingapp.data.pojo.RecipeInfo;
import org.peterkwan.udacity.bakingapp.viewmodel.RecipeStepViewModel;
import org.peterkwan.udacity.bakingapp.viewmodel.RecipeViewModel;

import java.util.List;

import butterknife.BindBool;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.graphics.Color.WHITE;
import static android.support.v4.media.session.MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS;
import static android.support.v4.media.session.MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS;
import static android.support.v4.media.session.PlaybackStateCompat.ACTION_PAUSE;
import static android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY;
import static android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY_PAUSE;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.google.android.exoplayer2.Player.STATE_READY;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnRecipeStepItemClickListener} interface
 * to handle interaction events.
 */
public class RecipeStepFragment extends Fragment implements PlaybackPreparer {

    private static final String TAG = RecipeStepFragment.class.getSimpleName();

    private static final String RECIPE_ID = "recipeId";
    private static final String STEP_ID = "stepId";
    private static final String RECIPE_NAME = "recipeName";
    private static final String PLAYBACK_POSITION = "playbackPosition";
    private static final String PLAYBACK_STATE = "playbackState";

    private static final int DEFAULT_STEP_ID = 0;
    private static final float PLAYBACK_SPEED = 1f;

    private OnRecipeStepItemClickListener mListener;
    private Unbinder unbinder;

    private int recipeId;
    private int stepId;
    private String recipeName;
    private int listIndex = DEFAULT_STEP_ID;

    private List<RecipeStep> recipeStepList;
    private SimpleExoPlayer player;
    private MediaSessionCompat mediaSession;
    private Context context;
    private Uri videoUri;
    private PlaybackStateCompat.Builder stateBuilder;
    private PlaybackStateCompat videoPlaybackState;
    private long playbackPosition;
    private boolean playbackState;

    @BindBool(R.bool.two_pane_layout)
    boolean isTwoPaneLayout;

    @BindString(R.string.recipe_step_format)
    String titleFormat;

    @BindString(R.string.play_video_error_message)
    String playbackErrorMessage;

    @BindString(R.string.no_video_error_message)
    String noVideoErrorMessage;

    @BindView(R.id.stepDetailTitleView)
    TextView titleTextView;

    @BindView(R.id.stepDetailShortDescriptionView)
    TextView shortDescriptionView;

    @BindView(R.id.stepDetailFullDescriptionView)
    TextView fullDescriptionView;

    @BindView(R.id.navNextImageView)
    ImageView navNextImageView;

    @BindView(R.id.navBeforeImageView)
    ImageView navBeforeImageView;

    @BindView(R.id.guideline)
    Guideline guideline;

    @BindView(R.id.playerView)
    PlayerView playerView;

    @BindView(R.id.thumbnailImageView)
    ImageView thumbnailImageView;

    @BindView(R.id.exo_error_message)
    TextView exoPlayerErrorMessage;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();

        if (args != null) {
            if (args.containsKey(RECIPE_ID))
                recipeId = args.getInt(RECIPE_ID);

            if (args.containsKey(RECIPE_NAME))
                recipeName = args.getString(RECIPE_NAME);

            if (args.containsKey(STEP_ID))
                stepId = args.getInt(STEP_ID);
        }

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(PLAYBACK_POSITION))
                playbackPosition = savedInstanceState.getLong(PLAYBACK_POSITION);

            if (savedInstanceState.containsKey(PLAYBACK_STATE))
                playbackState = savedInstanceState.getBoolean(PLAYBACK_STATE);
        }

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_recipe_step, container, false);

        unbinder = ButterKnife.bind(this, rootView);

        context = getContext();

        if (isTwoPaneLayout) {
            RecipeViewModel viewModel = ViewModelProviders.of(getActivity()).get(RecipeViewModel.class);
            viewModel.retrieveRecipe(recipeId).observe(this, new Observer<RecipeInfo>() {
                @Override
                public void onChanged(@Nullable RecipeInfo recipeInfo) {
                    if (recipeInfo != null) {
                        recipeStepList = recipeInfo.getStepList();
                        updateUI();
                    }
                }
            });
        }
        else {
            RecipeStepViewModel viewModel = ViewModelProviders.of(this).get(RecipeStepViewModel.class);
            viewModel.retrieveRecipeSteps(recipeId).observe(this, new Observer<List<RecipeStep>>() {
                @Override
                public void onChanged(@Nullable List<RecipeStep> stepList) {
                    recipeStepList = stepList;
                    updateUI();
                }
            });
        }

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRecipeStepItemClickListener) {
            mListener = (OnRecipeStepItemClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRecipeStepItemClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        player = null;
    }

    @Override
    public void onPause() {
        super.onPause();

        if (Util.SDK_INT <= Build.VERSION_CODES.M) {
            if (mediaSession != null)
                mediaSession.setActive(false);
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (Util.SDK_INT > Build.VERSION_CODES.M) {
            if (mediaSession != null)
                mediaSession.setActive(false);
            releasePlayer();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (player != null) {
            outState.putLong(PLAYBACK_POSITION, player.getCurrentPosition());
            outState.putBoolean(PLAYBACK_STATE, player.getPlayWhenReady());
        }
        super.onSaveInstanceState(outState);
    }

    @OnClick(R.id.navNextImageView)
    public void onNextStepClick() {
        if (mListener != null)
            mListener.onStepItemClicked(recipeStepList.get(++listIndex).getId(), recipeName);
    }

    @OnClick(R.id.navBeforeImageView)
    public void onPreviousStepClick() {
        if (mListener != null)
            mListener.onStepItemClicked(recipeStepList.get(--listIndex).getId(), recipeName);
    }

    @Override
    public void preparePlayback() {
        initializePlayer();
    }

    private void retrieveListIndex() {
        if (recipeStepList != null && !recipeStepList.isEmpty()) {
            for (int i = 0; i < recipeStepList.size(); i++) {
                if (recipeStepList.get(i).getId() == stepId) {
                    listIndex = i;
                    break;
                }
            }
        }
    }

    private void updateUI() {
        retrieveListIndex();

        RecipeStep recipeStep = recipeStepList.get(listIndex);
        String title = String.format(titleFormat, recipeStep.getId());

        titleTextView.setText(title);
        shortDescriptionView.setText(recipeStep.getShortDescription());
        fullDescriptionView.setText(recipeStep.getDescription());

        String videoUrl = recipeStep.getVideoUrl();
        String thumbnailUrl = recipeStep.getThumbnailUrl();

        if (!TextUtils.isEmpty(thumbnailUrl)) {
            Picasso.get()
                    .load(Uri.parse(thumbnailUrl))
                    .error(R.drawable.broken_icon)
                    .into(thumbnailImageView);

            playerView.setVisibility(View.GONE);
        }
        else {
            initializeVideoPlayerView(videoUrl);
            thumbnailImageView.setVisibility(View.GONE);
        }

        setComponentVisibility();
    }

    private void setComponentVisibility() {
        if (isTwoPaneLayout) {
            titleTextView.setVisibility(View.VISIBLE);
            shortDescriptionView.setVisibility(View.VISIBLE);
            fullDescriptionView.setVisibility(View.VISIBLE);
            guideline.setVisibility(View.VISIBLE);
            navNextImageView.setVisibility(View.INVISIBLE);
            navBeforeImageView.setVisibility(View.INVISIBLE);
        }
        else if (getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE) {
            titleTextView.setVisibility(View.INVISIBLE);
            shortDescriptionView.setVisibility(View.INVISIBLE);
            fullDescriptionView.setVisibility(View.INVISIBLE);
            guideline.setVisibility(View.GONE);
            navNextImageView.setVisibility(View.INVISIBLE);
            navBeforeImageView.setVisibility(View.INVISIBLE);
        }
        else {
            titleTextView.setVisibility(View.VISIBLE);
            shortDescriptionView.setVisibility(View.VISIBLE);
            fullDescriptionView.setVisibility(View.VISIBLE);
            guideline.setVisibility(View.VISIBLE);
            navNextImageView.setVisibility(recipeStepList == null || listIndex == recipeStepList.size() - 1 ? View.INVISIBLE : View.VISIBLE);
            navBeforeImageView.setVisibility(listIndex == 0 ? View.INVISIBLE : View.VISIBLE);
        }
    }

    private void releasePlayer() {
        if (player != null) {
            player.stop();
            player.release();
        }
    }

    private void initializePlayer() {
        if (player == null) {
            TrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory(new DefaultBandwidthMeter());
            TrackSelector trackSelector = new DefaultTrackSelector(trackSelectionFactory);
            player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
        }

        playerView.setPlayer(player);
        playerView.setPlaybackPreparer(this);

        exoPlayerErrorMessage.setTextColor(WHITE);

        String userAgent = Util.getUserAgent(context, TAG);
        MediaSource mediaSource = new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent)).createMediaSource(videoUri);
        player.prepare(mediaSource, playbackPosition != 0, false);

        player.seekTo(playbackPosition);
        player.setPlayWhenReady(playbackState);
        player.addListener(new MediaPlayerEventListener());
    }

    private void initializeMediaSession() {
        mediaSession = new MediaSessionCompat(context, TAG);
        mediaSession.setFlags(FLAG_HANDLES_MEDIA_BUTTONS | FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setMediaButtonReceiver(null);

        stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(ACTION_PLAY | ACTION_PAUSE | ACTION_PLAY_PAUSE);
        videoPlaybackState = stateBuilder.build();

        mediaSession.setPlaybackState(videoPlaybackState);
        mediaSession.setCallback(new MediaSessionCallback());
        mediaSession.setActive(true);
    }

    private void initializeVideoPlayerView(String videoUrl) {
        MediaPlayerErrorMessageProvider playerErrorMessageProvider = new MediaPlayerErrorMessageProvider();
        playerView.setErrorMessageProvider(playerErrorMessageProvider);
        playerView.requestFocus();

        if (videoUrl != null && !videoUrl.isEmpty())
            videoUri = Uri.parse(videoUrl);

        initializeMediaSession();
        initializePlayer();

        if (!isTwoPaneLayout && getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE)
            playerView.setLayoutParams(new ConstraintLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
    }

    class MediaPlayerEventListener extends Player.DefaultEventListener {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int state) {
            if (state == STATE_READY)
                stateBuilder.setState(playWhenReady ? STATE_PLAYING : STATE_PAUSED, player.getCurrentPosition(), PLAYBACK_SPEED);
            videoPlaybackState = stateBuilder.build();
            mediaSession.setPlaybackState(videoPlaybackState);
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            playerView.setUseController(false);
        }
    }

    class MediaPlayerErrorMessageProvider implements ErrorMessageProvider<ExoPlaybackException> {

        @Override
        public Pair<Integer, String> getErrorMessage(ExoPlaybackException throwable) {
            if (videoUri == null)
                return Pair.create(1, noVideoErrorMessage);
            else
                return Pair.create(0, playbackErrorMessage);
        }
    }

    class MediaSessionCallback extends MediaSessionCompat.Callback {

        @Override
        public void onPlay() {
            player.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            player.setPlayWhenReady(false);
        }
    }

}
