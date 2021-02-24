import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.App
import com.example.myapplication.R
import com.example.myapplication.data.datasource.remote.api.RecipeDTO
import com.example.myapplication.data.repository.Repository
import com.example.myapplication.navigation.mypage.MyMultiViewAdapter
import kotlinx.android.synthetic.main.fragment_mypage_follower.*

class MyPageFollowingFragment : Fragment() {

    private lateinit var v : View

    private var followingList = ArrayList<RecipeDTO.RecipeFinal>()
    private lateinit var rv_my_following : RecyclerView

    private val repository = Repository()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_mypage_following, container, false)

        setFollower()

        return v
    }

    private fun setFollower() {
        followingList.clear()

        rv_my_following = v.findViewById(R.id.rv_my_following)
        rv_my_following.layoutManager = LinearLayoutManager(App.instance, RecyclerView.VERTICAL, false)

        repository.getHomeRecipes(
            success = {
                it.run {
                    val data = it.list
                    followingList.addAll(data!!)
                    rv_my_following.adapter = MyMultiViewAdapter(2,followingList)

                    tv_my_follower_count.text = "전체 ${data.size}개"
                }
            },
            fail = {
                Log.d("fail", "fail fail fail")
            },
            queryType = "search",
            order = "latest"
        )
    }
}