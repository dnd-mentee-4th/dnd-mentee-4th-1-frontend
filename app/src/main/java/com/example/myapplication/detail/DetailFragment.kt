package com.example.myapplication.detail

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.myapplication.App
import com.example.myapplication.R
import com.example.myapplication.data.repository.Repository
import com.google.android.material.tabs.TabLayout


class DetailFragment : Fragment() {

    private lateinit var v: View

    internal lateinit var viewPagerPics: ViewPager
    internal lateinit var rvSteps: RecyclerView
    internal lateinit var tabLayout: TabLayout
    internal lateinit var ibRating:ImageButton
    internal lateinit var llStarRating:LinearLayout

    private lateinit var picsAdapter: DetailViewPagerPicsAdapter
    private lateinit var StepDescriptionAdapter: DetailStepsAdapter
    private lateinit var commentAdapter: DetailCommentAdapter
    private lateinit var tagAdapter: DetailTagAdapter
    private lateinit var mainIngredientAdapter: DetailMainIngredientAdapter
    private lateinit var subIngredientAdapter: DetailSubIngredientAdapter

    private var recipeId: Int = 0
    private var floatRatingAvgRound: Float = 0f

    private val repository = Repository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        v = inflater.inflate(R.layout.fragment_detail, container, false)


        getRecipeIdFromBeforeFragment()
        requestRecipeById(recipeId)
        requestCommentById(recipeId)

        setViewPagerPics()
        setStarRatingButton()
        setTagRecyclerView()
        setMainIngredients()
        setSubIngredients()
        setCommentRecyclerView()
        setViewPagerSteps()

        return v
    }


    /**  API Reciep Data 요청(id로) **/
    private fun requestRecipeById(recipeId: Int) {
        repository.getRecipeById(
            1,//TODO : recipeId로 변경 나중에
            success = {
                it.run {
                    val tvNickname = v.findViewById<TextView>(R.id.tv_uploader_name)
                    val tvTitle = v.findViewById<TextView>(R.id.tv_detail_title)
                    val tvDescription = v.findViewById<TextView>(R.id.tv_introduction)
                    val tvStarAverage = v.findViewById<TextView>(R.id.tv_star_rating)
                    val tvViewCount = v.findViewById<TextView>(R.id.tv_viewcount)
                    val tvRating = v.findViewById<TextView>(R.id.tv_star_rating2)
                    val tvTime = v.findViewById<TextView>(R.id.tv_time)

                    setProfilePic(it.data?.writer?.imageUrl)

                    tvNickname.text = it.data?.writer?.name
                    tvTitle.text = it.data?.title
                    tvDescription.text = it.data?.description
                    tvViewCount.text = it.data?.viewCount
                    tvTime.text = it.data?.time

                    setRatingBar(it.data?.starCount!!)
                    tvRating.text = "이 레시피는 ${floatRatingAvgRound}점 이에요!"
                    tvStarAverage.text = floatRatingAvgRound.toString()



                    // 어댑터 설정
                    picsAdapter.updateRecipeImage(it.data?.steps!!)
                    StepDescriptionAdapter.updateDescription(it.data?.steps!!)
                    tagAdapter.updateThemes(it.data?.themes)
                    mainIngredientAdapter.updateMainIngredients(it.data?.mainIngredients)
                    subIngredientAdapter.updateSubIngredients(it.data?.subIngredients)

                    picsAdapter.notifyDataSetChanged()
                    StepDescriptionAdapter.notifyDataSetChanged()
                    tagAdapter.notifyDataSetChanged()
                    mainIngredientAdapter.notifyDataSetChanged()
                    subIngredientAdapter.notifyDataSetChanged()
                }
            },
            fail = {
                Log.e("getRecipeById", "DetailFragment")
            }
        )
    }

    private fun requestCommentById(recipeId: Int) {
        repository.getCommentsById(
            1,//TODO : recipeId로 변경 나중에
            success = {
                it.run {
                    commentAdapter.updateComments(it.list!!)

                    commentAdapter.notifyDataSetChanged()
                }
            },
            fail = {
                Log.e("getCommentById", "DetailFragment")
            }

        )
    }

    private fun setProfilePic(imageUrl: String?) {
        val profilePic = v.findViewById<ImageView>(R.id.iv_uploader_profile)
        Glide.with(App.instance)
            .load(imageUrl)
            .placeholder(R.drawable.ic_face)
            .into(profilePic)
    }

    private fun setCommentProfilePic() {//TODO - 추후에 User API 배포 후 댓글 프로필 사진 설정
        val commentProfilePic = v.findViewById<ImageView>(R.id.iv_comment_profile)
        Glide.with(App.instance)
            .load(this)
            .placeholder(R.drawable.ic_face)
            .into(commentProfilePic)
    }

    /**  이전 Fragment에서 클릭된 Recipe Id 가져옴**/
    private fun getRecipeIdFromBeforeFragment() {
        recipeId = arguments!!.getInt("recipeId") // 전달한 key 값
    }


    private fun setRatingBar(ratingAverage: Double) {
        val floatRatingAvg = ratingAverage.toFloat()
        val ratingBar2 = v.findViewById(R.id.ratingbar2) as RatingBar
        val stars = ratingBar2.progressDrawable as LayerDrawable
        stars.getDrawable(2).setTint(Color.rgb(255,217,81))
        floatRatingAvgRound = Math.round(floatRatingAvg*10)/10f
        ratingBar2.rating= floatRatingAvgRound
    }

    /**  매인 재료 RecyclerView  **/
    private fun setMainIngredients() {
        mainIngredientAdapter = DetailMainIngredientAdapter()

        val rvMainIngredient = v.findViewById<RecyclerView>(R.id.rv_main_ingredient)
        rvMainIngredient.layoutManager = LinearLayoutManager(v.context, LinearLayoutManager.HORIZONTAL,false)
        rvMainIngredient.setHasFixedSize(true)
        rvMainIngredient.adapter = mainIngredientAdapter
    }

    private fun setSubIngredients() {
        subIngredientAdapter = DetailSubIngredientAdapter()

        val rvSubIngredient = v.findViewById<RecyclerView>(R.id.rv_sub_ingredient)
        rvSubIngredient.layoutManager = LinearLayoutManager(v.context, LinearLayoutManager.HORIZONTAL, false)
        rvSubIngredient.setHasFixedSize(true)
        rvSubIngredient.adapter = subIngredientAdapter
    }

    /**  RecyclerView : 요리순서  **/
    private fun setViewPagerSteps() {
        StepDescriptionAdapter = DetailStepsAdapter()

        rvSteps = v.findViewById<RecyclerView>(R.id.rv_steps)
        rvSteps.layoutManager = LinearLayoutManager(v.context,LinearLayoutManager.VERTICAL,false)
        rvSteps.setHasFixedSize(true)
        rvSteps.adapter = StepDescriptionAdapter

    }

    /**  RecyclerView : 댓글  **/
    private fun setCommentRecyclerView() {
        commentAdapter = DetailCommentAdapter()

        val rvComment = v.findViewById<RecyclerView>(R.id.rv_comment)
        rvComment.layoutManager = LinearLayoutManager(v.context,LinearLayoutManager.VERTICAL,false)
        rvComment.setHasFixedSize(true)
        rvComment.adapter = commentAdapter
    }

    /**  RecyclerView : 태그 버튼  **/
    private fun setTagRecyclerView() {
        tagAdapter = DetailTagAdapter()

        val rvTag = v.findViewById<RecyclerView>(R.id.rv_tag)
        rvTag.layoutManager = LinearLayoutManager(v.context,LinearLayoutManager.HORIZONTAL,false)
        rvTag.setHasFixedSize(true)
        rvTag.adapter = tagAdapter
    }


    private fun setStarRatingButton() {
        ibRating = v.findViewById<ImageButton>(R.id.iv_like)
        ibRating.setOnClickListener {//평점주기 버튼 클릭 리스너

            val dialog = Dialog(v.context);
            dialog.setCancelable(false);
            // 다이얼로그 화면 설정
            dialog.setContentView(R.layout.dialogue_star_rating)
            dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

            val rating : RatingBar = dialog.findViewById(R.id.ratingbar_dialogue);
            val btn_ok : Button = dialog.findViewById(R.id.btn_ok);
            val btn_cancel : Button = dialog.findViewById(R.id.btn_cancel)

            rating.setRating(3f) // 레이팅 바에 기본값 채우기
            rating.setIsIndicator(false)// 사용자가 임의로 별점을 바꿀수 있도록 허가하는 메서드
            rating.setStepSize(1f)// 한칸당 1 점으로 할당

            val starsInRatingDialogue = rating.progressDrawable as LayerDrawable
            starsInRatingDialogue.getDrawable(2).setTint(Color.rgb(255,217,81))

            btn_ok.setOnClickListener {
                dialog.dismiss()
                ibRating.setBackgroundResource(R.drawable.ic_home);
                //TODO : 서버 요청
            }
            btn_cancel.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show();
        }
    }

    private fun setViewPagerPics() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedElementEnterTransition = TransitionInflater
                .from(context)
                .inflateTransition(R.transition.shared_image)// 바꿀 수 있음
        }
        viewPagerPics = v.findViewById<ViewPager>(R.id.vp_recipes)
        ViewCompat.setTransitionName(viewPagerPics, "@string/transition_random_to_detail")

        tabLayout = v.findViewById<TabLayout>(R.id.tab_layout)

        picsAdapter = DetailViewPagerPicsAdapter(v.context)
        viewPagerPics.adapter = picsAdapter

        tabLayout.setupWithViewPager(viewPagerPics)//Circle Indicator 추가
        sharedElementEnterTransition = TransitionInflater.from(requireContext()).inflateTransition(R.transition.shared_image)
    }
}