package yuanren.tvsamrtwatch.smartwatchinteractions.data;

import android.util.Log;
import android.view.KeyEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.SplittableRandom;

import yuanren.tvsamrtwatch.smartwatchinteractions.models.pojo.Movie;
import yuanren.tvsamrtwatch.smartwatchinteractions.models.pojo.XRayItem;

public final class MovieList {
    private static final String TAG = "MovieList";
    public static final int NUM_REAL_MOVIE = 2; // the number of real movie in each row
    public static final int NUM_DUMMY_MOVIE = 5; // the number of distinct dummy movies (change in func setUpDummyMovies accordingly)
    public static final int NUM_COLS = 20;
    public static final int NUM_MOVIE_CATEGORY = 12; // change MOVIE_CATEGORY accordingly
    private static  final String KEY_TYPE = "type";
    private static  final String KEY_NAME = "name";
    private static  final String KEY_IMAGE = "image";
    private static  final String KEY_DESCRIPTION = "description";
    private static  final String KEY_MERCHANDISE = "merchandise";

    private static List<Movie> list;
//    private static List<Movie> dummyList;
    private static int index = 0;

    private static SplittableRandom splittableRandom;
    public static int[] randomPositions;

    public static int getIndex() {
        return index;
    }

    public static Movie getFirstMovie() {
        return list == null ? null : list.get(0);
    }

    public static Movie getMovie(int id) {
        return list.get(id);
    }

    public static boolean isToOutOfRow(int keyEvent) {
        if (keyEvent == KeyEvent.KEYCODE_DPAD_UP) {
            if (index + NUM_COLS >= NUM_COLS * NUM_MOVIE_CATEGORY) {
                return true;
            }
        }
        if (keyEvent == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (index - NUM_COLS < 0) {
                return true;
            }
        }
        return false;
    }

    public static Movie getNextMovie(int keyEvent) {
        switch (keyEvent) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                index = Math.min(NUM_COLS * NUM_MOVIE_CATEGORY, index + 1);
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                index = Math.max(0, index - 1);
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                index = index + NUM_COLS >= NUM_COLS * NUM_MOVIE_CATEGORY ? index : index + NUM_COLS;
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                index = index - NUM_COLS < 0 ? index : index - NUM_COLS;
                break;
            default:
                return null;
        }

//        if (index % 20 == 0 || index % 20 == 1) { // first two movies
//            int i =  (index / 20) * 2 + index % 20;
//            return list.get(i);
//        } else {
//            int i = (index % 20 - 2) % NUM_DUMMY_MOVIE;
//            return dummyList.get(i);
//        }
        return list.get(index);
    }

    // change NUM_MOVIE_CATEGORY accordingly
    public static final String MOVIE_CATEGORY[] = {
            "Dramas",
            "Romance",
            "Comedies",
            "Action",
            "Horror",
            "Sci-Fi",
            "Adventure",
            "Fantasy",
            "Musicals",
            "Mysteries",
            "Sports",
            "Crime"
    };

    public static List<Movie> setUpMovies() {
        /** generate random positions for real movies */
        list = new ArrayList<>();
        randomPositions = new int[NUM_MOVIE_CATEGORY * NUM_REAL_MOVIE];
        for (int i = 0; i < randomPositions.length; i += 2) {
            // get grid position for the two movies in the same category
            int coarse1 = getRandomInt(0, 3);
            int coarse2 = getRandomInt(0, 3);;
            while (coarse1 == coarse2) {
                coarse2 = getRandomInt(0, 3);
            }

            // get precise position for each movie in the same category
            int precise1 = getRandomPrecise(coarse1);
            int precise2 = getRandomPrecise(coarse2);

            randomPositions[i] = precise1;
            randomPositions[i + 1] = i + 1 < randomPositions.length ? precise2 : randomPositions.length - 1;
        }
        Log.d(TAG, print(randomPositions));  // debug use

        /** fill real movies at the random position and dummy movies in the rest */
        ListIterator<Movie> reals = setUpRealMovies().listIterator();
        ListIterator<Movie>  dummies = setUpDummyMovies((NUM_COLS - NUM_REAL_MOVIE) * NUM_MOVIE_CATEGORY).listIterator();
        for (int row = 0; row < NUM_MOVIE_CATEGORY; ++row) {
            for (int col = 0; col < NUM_COLS; ++col) {
                if (col == randomPositions[row * 2] || col == randomPositions[row * 2 + 1]) {
                    Movie movie = reals.next();
                    movie.setPosition(col);
                    list.add(movie);
                } else {
                    list.add(dummies.next());
                }
            }
        }

        return list;
    }

    private static String print(int[] data) {
        String s = "";
        for (int i = 0; i < data.length - 1; ++i) {
            s += data[i] + ", ";
        }

        s += data[data.length - 1];
        return s;
    }

    private static int getRandomInt(int min, int max){
        if (splittableRandom == null) {
            splittableRandom = new SplittableRandom();
        }
        return splittableRandom.nextInt(min, max);
    }

    private static int getRandomPrecise(int gridPosition) {
        switch (gridPosition) {
            case 0:
                return getRandomInt(0, 7);
            case 1:
                return getRandomInt(7, 14);
            default:
                return getRandomInt(14, 20);
        }
    }

    private static List<Movie> setUpRealMovies() {
        List<Movie> reals = new ArrayList<>();
        String title[] = {
                "The King's Man",
                "Red Notice",
                "Jumanji",
                "Uncharted",
                "The Devil Wears Prada",
                "The Wolf of Wall Street",
                "Venom",
                "Iron man",
                "Harry Potter and the Prisoner of Azkaban",
                "Fantastic Beasts and Where to Find Them",
                "Insomnia",
                "Fall",
                "Mama Mia",
                "Lala Land",
                "Sherlock Holmes",
                "The Da Vinci Code",
                "Flipped",
                "Crazy Rich Asians",
                "Inception",
                "The Adam Project",
                "Space Jam",
                "Million Dollar Baby",
                "Death on the Nile",
                "Pain Hustler"
        };

        String description = "Fusce id nisi turpis. Praesent viverra bibendum semper. "
                + "Donec tristique, orci sed semper lacinia, quam erat rhoncus massa, non congue tellus est "
                + "quis tellus. Sed mollis orci venenatis quam scelerisque accumsan. Curabitur a massa sit "
                + "amet mi accumsan mollis sed et magna. Vivamus sed aliquam risus. Nulla eget dolor in elit "
                + "facilisis mattis. Ut aliquet luctus lacus. Phasellus nec commodo erat. Praesent tempus id "
                + "lectus ac scelerisque. Maecenas pretium cursus lectus id volutpat.";
        String studio[] = {
                "20th Century",
                "Universal Pictures",
                "Columbia Pictures",
                "Columbia Pictures",
                "20th Century Fox",
                "Red Granite Pictures",
                "Sony Pictures",
                "Marvel Studios",
                "Warner Bros. Pictures",
                "Warner Bros. Pictures",
                "Warner Bros. Pictures",
                "Tea Shop Productions",
                "Universal Pictures",
                "Lionsgate",
                "Silver Pictures",
                "Columbia Pictures",
                "Warner Bros. Pictures",
                "Warner Bros. Pictures",
                "Warner Bros. Pictures",
                "Netflix",
                "Warner Bros. Pictures",
                "Warner Bros. Pictures",
                "20th Century Studios",
                "Netflix",
        };
        String category[] = {
                MOVIE_CATEGORY[3],
                MOVIE_CATEGORY[3],
                MOVIE_CATEGORY[6],
                MOVIE_CATEGORY[6],
                MOVIE_CATEGORY[2],
                MOVIE_CATEGORY[2],
                MOVIE_CATEGORY[0],
                MOVIE_CATEGORY[0],
                MOVIE_CATEGORY[7],
                MOVIE_CATEGORY[7],
                MOVIE_CATEGORY[4],
                MOVIE_CATEGORY[4],
                MOVIE_CATEGORY[8],
                MOVIE_CATEGORY[8],
                MOVIE_CATEGORY[9],
                MOVIE_CATEGORY[9],
                MOVIE_CATEGORY[1],
                MOVIE_CATEGORY[1],
                MOVIE_CATEGORY[5],
                MOVIE_CATEGORY[5],
                MOVIE_CATEGORY[10],
                MOVIE_CATEGORY[10],
                MOVIE_CATEGORY[11],
                MOVIE_CATEGORY[11],
        };
        String videoUrl[] = {
                "https://streamable.com/l/59by34/mp4-high.mp4",  // Kings Man
                "https://streamable.com/l/v9e0nk/mp4-high.mp4",  // Red Notice

                "https://streamable.com/l/oxud0a/mp4-high.mp4",  // Jumanji
                "https://streamable.com/l/xuehud/mp4-high.mp4",  // Uncharted

                "https://streamable.com/l/5srodl/mp4-high.mp4",  // The Devil Wears Prada
                "https://streamable.com/l/fivagj/mp4-high.mp4",  // The wolf of Wall Street

                "https://streamable.com/l/mhrrwj/mp4-high.mp4",  // Venom
                "https://streamable.com/l/oohm1o/mp4-high.mp4",  // Iron Man

                "https://streamable.com/l/mnxbn8/mp4-high.mp4",  // Harry Potter 3
                "https://streamable.com/l/2l37xs/mp4-high.mp4",  // Fantastic Beasts 1

                "https://streamable.com/l/slxtfy/mp4-high.mp4",  // Insomnia
                "https://streamable.com/l/h43q1z/mp4-high.mp4",  // Fall

                "https://streamable.com/l/cleqsc/mp4-high.mp4",  // Mama Mia
                "https://streamable.com/l/w3xnvp/mp4-high.mp4",  // Lala Land

                "https://streamable.com/l/ypqkr3/mp4-high.mp4",  // Sherlock Holmes
                "https://streamable.com/l/uzr6i2/mp4-high.mp4",  // The Da Vinci Code

                "https://streamable.com/l/mpid4f/mp4-high.mp4",  // Flipped
                "https://streamable.com/l/24bwrf/mp4-high.mp4",  // Crazy Rich Asians

                "https://streamable.com/l/r5v0uu/mp4-high.mp4",  // Inception
                "https://streamable.com/l/p0qlbd/mp4-high.mp4",   // The Adam Project

                "https://streamable.com/l/h8d1dw/mp4-high.mp4", // Space Jam
                "https://streamable.com/l/pefotk/mp4-high.mp4", // Million Dollar Baby

                "https://streamable.com/l/xzy8gi/mp4-high.mp4", // Death on the Nile
                "https://streamable.com/l/49qarq/mp4-high.mp4" // Pain Hustler
        };
        String bgImageUrl[] = {
                "https://orgoglionerd.it/wp-content/uploads/2021/12/kingsman-3.jpg",
                "https://occ-0-34-1555.1.nflxso.net/dnm/api/v6/6AYY37jfdO6hpXcMjf9Yu5cnmO0/AAAABVkcDtE1_UaJBGFQUfxakjzhYdT1L4kso24uZS0ASl_faURPBSGzY_Mxbx1KEcNbZr3ZqCatG-0zi2k3L4oBXenznQrrROKJQqdu.jpg?r=590",

                "https://imageio.forbes.com/blogs-images/scottmendelson/files/2017/12/1511829518078_215329_cops_21-1200x588.jpg?format=jpg&width=1200",
                "https://www.whats-on-netflix.com/wp-content/uploads/2022/08/best-new-movies-on-netflix-this-week-august-5th-2022.webp",

                "https://www.refinery29.com/images/8436438.jpg?crop=40%3A21",
                "https://assets.playbill.com/editorial/a728180f0359e96ac3b95916cda901ce-the-wolf-of-wall-street.-London-immersive-production-2019.-Credit-photography-by-Michael-Wharley-design-by-Rebecca-Pitt.jpg",

                "https://www.justwatch.com/images/backdrop/83182674/s1920/venom-2018.webp",
                "https://www.quirkybyte.com/wp-content/uploads/2016/08/iron-man.png",

                "https://static1.cbrimages.com/wordpress/wp-content/uploads/2020/05/harry-potter-and-the-prisoner-of-azkaban-1400x700.jpg",
                "https://media.glamour.com/photos/595a489b76072428dce95d1f/master/w_2560%2Cc_limit/fantastic%2520beasts%2520lede.jpg",

                "https://i0.wp.com/moviesandmania.com/wp-content/uploads/2020/11/Insomnia-movie-film-thriller-murder-mystery-2002-Christopher-Nolan-reviews-Al-Pacino.png?fit=3052%2C1318&ssl=1",
                "https://media.socastsrm.com/wordpress/wp-content/blogs.dir/460/files/2022/09/banner-fall.jpg",

                "https://cache.pressmailing.net/content/179a4950-52c1-4799-9068-6bef6958e96e/RTLZWEI_Mamma-Mia.jpg",
                "https://www.theasburycollegian.com/wp-content/uploads/2023/02/via-Getty-Images.jpeg",

                "https://static1.colliderimages.com/wordpress/wp-content/uploads/2023/02/sherlock-holmes-jude-law-robert-downey-jr.jpg",
                "https://imgsrc.cineserie.com/2021/10/822467.jpg?ver=1",

                "https://images-3.wuaki.tv/system/shots/184836/original/snapshot-1590658133.jpeg",
                "https://gugimages.s3.us-east-2.amazonaws.com/wp-content/uploads/2020/06/21122846/Crazy-Rich-Asians-background.jpg",

                "https://i4.hurimg.com/i/hurriyet/75/1200x675/5f5965580f25440e585d7e6f.jpg",
                "https://i0.wp.com/615film.com/wp-content/uploads/2022/03/the-adam-project-header-image.jpg?fit=1280%2C720&ssl=1",

                "https://cdn.mos.cms.futurecdn.net/aj3sYEDRRaEaaUfK29ae9Y.jpg",
                "https://static.filmin.es/images/media/4226/5/still_0_3_790x398.webp",

                "https://images-na.ssl-images-amazon.com/images/S/pv-target-images/8b3d35c268f130d43d1c35d44305da1375c8d982561ae24aabd435563d145fcf._RI_TTW_SX1080_FMjpg_.jpg",
                "https://www.avforums.com/styles/avf/editorial/block//4c282660a8428f848b7454699af4a5bb_3x3.jpg"
        };
        String cardImageUrl[] = {
                "https://image.tmdb.org/t/p/w300/qzUIOTk0E3F1zjvYjcBRTKUTgf9.jpg",  // Kingsman
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS-NPoAHHYnD7NanifrvDSeN5tWLld6B3WP_vvs2hvFVMDB-7acfRajLnNR7oEwP3dLBb4&usqp=CAU",  // Red Notice

                "https://img3.hulu.com/user/v3/artwork/3db7ecba-9c35-4663-bed2-15b2725709c5?base_image_bucket_name=image_manager&base_image=eff07ffa-ac26-462e-8b42-4f14f1c6226e&size=458x687&format=jpeg",  // Jumanji
                "https://assets-prd.ignimgs.com/2022/01/13/uncharted-newbutton-1642092379328.jpg",  //Uncharted

                "https://i.insider.com/60dba0f08a6397001257af9f?width=1000&format=jpeg&auto=webp",  // The Devil Wears Prada
                "https://media.newyorker.com/photos/590951571c7a8e33fb38a2e8/master/w_2560%2Cc_limit/wallstreet-580.jpg",  // The Wolf of Wall Street

                "https://resizing.flixster.com/ydEdPTmh6k_bkuWwskRYT9CDnCk=/300x300/v2/https://flxt.tmsimg.com/assets/p13937884_v_v8_aa.jpg",  // Venom
                "https://www.quirkybyte.com/wp-content/uploads/2016/08/iron-man.png",  //Iron Man

                "https://i.pinimg.com/1200x/c1/df/39/c1df3993439b5a55c11f8b0f729ad102.jpg",  // HP3
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSuijfVuXbPWHRCGB843UoJBhxkwrfw2VybzNKMzog71VyUB1mEDkg0j9Fk7AzWo2ItD-g&usqp=CAU",  // FB

                "https://basementrejects.com/wp-content/uploads/2012/07/insomnia-fog.jpg",  // Insomnia
                "https://upload.wikimedia.org/wikipedia/en/2/2a/Fall_%282022_film%29.jpg",  // Fall

                "https://i.pinimg.com/640x/c7/7a/6b/c77a6bdff30f5d93abbbdab1e3466ae7.jpg",  // Mamamia
                "https://sojo.net/sites/default/files/styles/large_rectangle_crop/public/blog/14138269_576769935842944_1998357375766171876_o.jpg?itok=2EqpOLdj",  // Lalaland

                "https://is4-ssl.mzstatic.com/image/thumb/Features123/v4/b5/3a/27/b53a271e-38dc-b59e-e6dc-f8bcffbeb731/mzl.ioafrsek.png/1200x675mf.jpg",  // Sherlock Holmes
                "https://cdn-o.fishpond.com/0284/768/355/1398570872/original.jpeg",  // The Da Vinci COde

                "https://www.mercurynews.com/wp-content/uploads/2016/08/20100910_105353_flipped2.jpg?w=640",  // Flipped
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQbv0t6O-EJar3UUAdtXQtbSWEDjHCCK7eCkb8X4CjyaSPaUzmjTGf8dix1Ll-tnms2B-4&usqp=CAU",  // Crazy Rich Asians

                "https://assets.mycast.io/posters/inception-2-the-big-under-2023-fan-casting-poster-66202-large.jpg?1605643123",  // Inception
                "https://image.tmdb.org/t/p/original/wFjboE0aFZNbVOF05fzrka9Fqyx.jpg",  // The Adam Project

                "https://m.media-amazon.com/images/M/MV5BOWEzNTQxZDctZTcwOS00Zjk4LWExMGMtYjg1NDNjZDZlMDg4XkEyXkFqcGdeQXVyMTQyMTMwOTk0._V1_.jpg",  // Space Jam
                "https://images.jdmagicbox.com/comp/jd_social/news/2018jul17/image-84441-nwd5h8xu2l.jpg",  // Million Dollar Baby

                "https://lumiere-a.akamaihd.net/v1/images/07bmech_dotn_pay1alt_1sht_displus_331cd907.jpeg?region=0,158,743,416",  // Death on the Nile
                "https://images.squarespace-cdn.com/content/63bb3e8a824d7e2f7eedf0d3/1696948409227-1MKZV3C6W7N2TUQ0UZ70/Pain%2BHustlers%2B4.jpg?content-type=image%2Fjpeg"  // Pain Hustler
        };

        int n = title.length;
        for (int index = 0; index < n; ++index) {
            reals.add(
                    buildMovieInfo(
                            (long) index,
                            title[index],
                            description,
                            studio[index],
                            category[index],
                            videoUrl[index],
                            cardImageUrl[index],
                            bgImageUrl[index],
                            setUpXRayItems()));
        }

        // set up make up dummy movies for each category (only 2 real movie in each category)
//        setUpDummyMovies(n - NUM_REAL_MOVIE);
        return reals;
    }

    private static Movie buildMovieInfo(
            Long id,
            String title,
            String description,
            String studio,
            String category,
            String videoUrl,
            String cardImageUrl,
            String backgroundImageUrl,
            List<Map<String, List<String>>> xRayItems) {
        Movie movie = new Movie();
//        movie.setId(count++);
        movie.setId(id);
        movie.setTitle(title);
        movie.setDescription(description);
        movie.setStudio(studio);
        movie.setCategory(category);
        movie.setCardImageUrl(cardImageUrl);
        movie.setBackgroundImageUrl(backgroundImageUrl);
        movie.setVideoUrl(videoUrl);

        Map<String, List<String>> xRayItemForMovie = xRayItems.get((id.intValue()));
//        Map<String, List<String>> xRayItemForMovie = xRayItems.get(0);
        List<String> types = xRayItemForMovie.get(KEY_TYPE);
        List<String> names = xRayItemForMovie.get(KEY_NAME);
        List<String> images = xRayItemForMovie.get(KEY_IMAGE);
        List<String> descriptions = xRayItemForMovie.get(KEY_DESCRIPTION);
        List<String> merchandises = xRayItemForMovie.get(KEY_MERCHANDISE);

        List<XRayItem> items = new ArrayList<>();
        for (int i = 0; i < names.size(); ++i) {
            XRayItem item = new XRayItem(id, i, types.get(i), names.get(i), images.get(i), descriptions.get(i), merchandises.get(i));
            items.add(item);
        }
        movie.setXRayItems(items);

        return movie;
    }

    private static List<Map<String, List<String>>> setUpXRayItems() {
        List<Map<String, List<String>>> listOfItems = new ArrayList<>();

        // The king's Man
        String xRay1Type[] = {
                "0",
                "0",
                "1",
                "1",
                "1",
                "1",
                "1"
        };
        String xRay1Name[] = {
                "Taron Egerton",
                "Sofia Boutella",
                "Cutler and Gross Glasses",
                "Savile Row Suits",
                "Oxford Brogues Shoes",
                "Drake's Tie",
                "Tag Heuer Watch",
        };
        String xRay1ImageUrl[] = {
                "https://flxt.tmsimg.com/assets/765421_v9_bd.jpg",
                "https://flxt.tmsimg.com/assets/327103_v9_bb.jpg",
                "https://cdn-images.farfetch-contents.com/11/88/16/03/11881603_9297567_1000.jpg",
                "https://savilerowco.com/media/catalog/product/cache/dcdc46754122207d7695de0b04975a01/n/a/navy-wool-blend-tailored-suit-msuit337nav_model_1.jpg",
                "https://images.squarespace-cdn.com/content/v1/5f4e4231f7c05656d0e2ef40/1641562901123-LL3V6VRARJAZPYKSHIJO/Shoe+5+Front.jpg?format=2500w",
                "https://cdn.shopify.com/s/files/1/0649/6603/3653/products/FRE80R-02053-011-1_1080x.jpg?v=1665494255",
                "https://www.tagheuer.com/on/demandware.static/-/Sites-tagheuer-master/default/dw5044426c/TAG_Heuer_Connected_/SBR8A10.BT6259/SBR8A10.BT6259_0913.png?impolicy=resize&width=1328&height=996",

        };
        String xRay1Description[] = {
                "Taron Egerton;• Born in United Kingdom\n• Age: 33 (1989)\n• Height: 5′ 9″\n• Nationality: British;Taron Egerton is a Welsh actor. He is the recipient of a Golden Globe Award, and has received nominations for a Grammy Award and two British Academy Film Awards.\n\nHe gained recognition for his starring role as a spy recruit in the action comedy film Kingsman: The Secret Service (2014) and its sequel Kingsman: The Golden Circle (2017). ",
                "Sofia Boutella;• Born in Algeria\n• Age: 40 (1982)\n• Height: 5′ 5″\n• Nationality: Algerian, French;Sofia Boutella is an Algerian actress, model, and dancer.",

                "Cutler and Gross GR03 Square Glasses;$439;• Lens Width: 50mm\n• Bridge Width: 18mm\n• Temple Length: 145mm\n\nThis optical frame is packed with considered design details and technical elements which elevate both its form and function. Angular exterior bevelling, coupled with a high nose bridge reflect the stylistic inspirations for this frame, whilst weight-saving interior milling maximises wearability and comfort.",
                "Navy Wool-Blend Tailored Suit;$311;• Material: 43% virgin wool, 53% polyester, 4% lycra\n• Weight: 280 g\n\nDurable and lightweight, this two-piece navy suit is ideal for business trips abroad. Designed on Savile Row, it has contrast burgundy lining and features elastane within the fabric composition – allowing any creases to drop out easily.",
                "Edward in Black Calf with Rubber Sole Shoes;$575;• Material: Leather linings\n• Comes with dust bags\n• Handmade in England\n\nThese black calf Oxford semi-brogues featuring brogue detail on cap, vamp & top of quarters is a essential business shoe. Flawlessly hand-finished in England in a round-toe shape, this slick pair will put you in good stead for years to come.",
                "Navy and Blue Stripe Repp Silk Tie;$225;• Material: 100% silk\n• Size: 8cm x 147cm\n• Handmade in London, England\n\nA striped repp silk tie is one of the most enduring pieces of neckwear available, redolent of earlier periods of style, while retaining a distinctly modern feel.",
                "Tag Heuer Connected Modular 45 Watch;$2050;• Model: Connected Calibre E4\n• Width: 45 mm\n\nStriking the perfect blend between innovation and high-end watchmaking, this TAG Heuer Connected Watch reveals an elegant 45mm steel case with sharp and sporty lugs, and integrated steel chronograph pushers. A new dimension where luxury meets performance."
        };
        String xRay1Merchandise[] = {
                "",
                "",
                "amazon target walmart",
                "amazon target walmart",
                "amazon target walmart",
                "amazon target walmart",
                "amazon bestbuy costco"
        };

        Map<String, List<String>> entry1 = new HashMap<>();
        entry1.put(KEY_TYPE, Arrays.asList(xRay1Type));
        entry1.put(KEY_NAME, Arrays.asList(xRay1Name));
        entry1.put(KEY_IMAGE, Arrays.asList(xRay1ImageUrl));
        entry1.put(KEY_DESCRIPTION, Arrays.asList(xRay1Description));
        entry1.put(KEY_MERCHANDISE, Arrays.asList(xRay1Merchandise));
        listOfItems.add(entry1);




        // Red Notice
        String xRay2Type[] = {
                "0",
                "0",
                "1",
                "1",
                "1",
                "1",
                "1"
        };
        String xRay2Name[] = {
                "Dwayne Johnson",
                "Ryan Reynolds",

                "Brunello Cucinelli Blazer",
                "Suunto Core Watch",
                "iPhone SE",
                "iPad mini 2",
                "Gitzo Camera Tripod",
        };
        String xRay2ImageUrl[] = {
                "https://flxt.tmsimg.com/assets/235135_v9_bb.jpg",
                "https://t2.gstatic.com/licensed-image?q=tbn:ANd9GcRDIJ-PkZ_U6WOLo7fm-UVwSZbyFb8fu5TVmvvErBzsxb_TmMMdJEyOMBT53C7zqxvuqDATtVONl_l5zPM",

                "https://cdn-images.farfetch-contents.com/18/64/56/76/18645676_41150734_1000.jpg",
                "https://www.suunto.com/globalassets/productimages/suunto-core-alu-deep-black/suunto-core-alu-deep-black-3946.png?height=1100&format=jpg&bgcolor=f6f6f6",
                "https://m.media-amazon.com/images/I/61TOWf11+jL.jpg",
                "https://www.att.com/scmsassets/global/devices/tablets/apple/apple-ipad-mini-2021/defaultimage/purple-hero-zoom.png",
                "https://m.media-amazon.com/images/I/41qqMswbp4L._AC_SX679_.jpg"
        };
        String xRay2Description[] = {
                "Dwayne Johnson;• Born in Hayward, CA, America\n• Age: 50 (1972)\n• Height: 6′ 5″\n• Nationality: American;Dwayne Douglas Johnson, also known by his ring name The Rock, is an American actor and former professional wrestler.",
                "Ryan Reynolds;• Born in Vancouver, Canada\n• Age: 46 (1976)\n• Height: 6′ 2″\n• Nationality: American, Canadian;Ryan Rodney Reynolds began his career starring in the Canadian teen soap opera Hillside, and had minor roles before landing the lead role on the sitcom Two Guys and a Girl between 1998 and 2001.",

                "Brunello Cucinelli Silk-Lapel Single-Breasted Llazer;$4995;• Outer Material: 100% cotton\n• Lining Material: 100% cupro\n• Dry clean only\n\nMade in Italy.",
                "Suunto Core Alu Deep Black;$229;• Measurements: 49.1 x 49.1 x 14.5 mm\n• Weight: 79 g\n• Bezel Material: Aluminum\n• Glass material: Mineral crystal\n\nSuunto Core Premium combines modern design with the essential outdoor functions. Choose rigid natural stainless steel with sapphire crystal glass for added durability, or the elegant aluminum cases for less weight on the wrist.",
                "iPhone SE;$479;• Size: 128 GB\n• Color: Midnight\n• Display:4.7-inch Retina HD display\n• Height:138.4 mm\n• Width: 67.3 mm\n• Depth: 7.3 mm\n• Weight: 144 g",
                "iPad Mini 2;$649;• Size: 256 GB\n• Color: Space Grey\n• Display: 8.3” Liquid Retina display²\n• Height: 200 mm\n• Width: 134.7 mm\n• Depth: 0.5 mm\n• Weight: 331 g",
                "Gitzo Camera Tripod;$1616.88;• Dimensions: 30.9 x 7.6 x 6.6 inches\n• Weight: 5.8 pounds\n\nThe Gitzo Carbon Fiber Tripod with  3-Way Fluid Head Kit is a 4-section support with carbon fiber eXact tubing-constructed legs. The twist leg locks unlock with a quarter turn, enabling you to extend the legs along with the rapid center column"
        };
        String xRay2Merchandise[] = {
                "",
                "",
                "amazon target walmart",
                "amazon target walmart",
                "apple amazon bestbuy",
                "apple amazon bestbuy",
                "amazon bestbuy costco"
        };
        Map<String, List<String>> entry2 = new HashMap<>();
        entry2.put(KEY_TYPE, Arrays.asList(xRay2Type));
        entry2.put(KEY_NAME, Arrays.asList(xRay2Name));
        entry2.put(KEY_IMAGE, Arrays.asList(xRay2ImageUrl));
        entry2.put(KEY_DESCRIPTION, Arrays.asList(xRay2Description));
        entry2.put(KEY_MERCHANDISE, Arrays.asList(xRay2Merchandise));
        listOfItems.add(entry2);




        // Jumanji
        String xRay3Type[] = {
                "0",
                "0",
                "0",
                "1",
                "1",
                "1",
                "1",
                "1"
        };
        String xRay3Name[] = {
                "Dwayne Johnson",
                "Kevin Hart",
                "Karen Gillan",
                "Timberland Mill Shirt ",
                "Riparo Half Finger Gloves",
                "Army Camo Multicam Pants",
                "Tru-Spec Men's Gen-ii Boonie",
                "Fitted Short Sleeve Crop Top",
        };
        String xRay3ImageUrl[] = {
                "https://flxt.tmsimg.com/assets/235135_v9_bb.jpg",
                "https://assets.fxnetworks.com/cms-next/production/cms/2021/05/26/web_crew_kevinhart_dave_570x698.jpg",
                "https://static.tvtropes.org/pmwiki/pub/images/dw_kg.png",
                "https://m.media-amazon.com/images/I/713Gok5YfvL._AC_UY879_.jpg",
                "https://m.media-amazon.com/images/I/81QqXeRio+L._AC_SX679_.jpg",
                "https://m.media-amazon.com/images/I/718UGtRaAlL._AC_UY879_.jpg",
                "https://assets.cat5.com/images/catalog/products/5/9/9/9/2/0-650-tru-spec-poly-cotton-ripstop-gen-ii-adjustable-boonie-khaki.jpg",
                "https://m.media-amazon.com/images/I/610b7cSuRaL._AC_UY879_.jpg"
        };
        String xRay3Description[] = {
                "Dwayne Johnson;• Born in Hayward, CA, America\n• Age: 50 (1972)\n• Height: 6′ 5″\n• Nationality: American;Dwayne Douglas Johnson, also known by his ring name The Rock, is an American actor and former professional wrestler.",
                "Kevin Hart;• Born in Philadelphia, PA, America\n• Age: 43 (1979)\n• Height: 5′ 2″\n• Nationality: American;Kevin Darnell Hart is an American comedian and actor. Originally known as a stand-up comedian, he has since starred in Hollywood films and on TV. He has also released several well-received comedy albums.",
                "Karen Gillan;• Born in Inverness, United Kingdom\n• Age: 35 (1987)\n• Height: 5′ 11″\n• Nationality:British, Scottish;Karen Sheila Gillan is a Scottish actress. She gained recognition for her work in British film and television, particularly for playing Amy Pond, a primary companion to the Eleventh Doctor in the science fiction series Doctor Who, for which she received several awards and nominations.",

                "Timberland Mill River Linen Cargo SS Shirt ;$90;• Material: 100% cotton\n• Machine wash\n\nThis durable short-sleeve work shirt comes standard with built-in stretch, so whatever you're doing, it's easier to do.",
                "Riparo Motorsports Half Finger Gloves;$41.5;• Material: Leather\n• Color: Black\n• Sport Type: Cycling\n\nThese genuine leather gloves have the quality and the features you would expect from a pair of quality driving gloves , including soft supple leather and a snap closure on the wrist strap.",
                "Army Camo Multicam Pants;$99.99;• Material: 50% cotton, 50% nylon\n• Machine wash\n\nThere are some core features include: double layer seat /knee areas for durability ,a rugged metal YKK zipper is used for the fly, knees are slightly articulated, and a simple velcro strap system in built into the waist to allow for size adjustments.",
                "Tru-Spec Men's Gen-ii Adjustable Boonie;$16.5;• Material: 100% cotton\n• Hand wash only\n\nThese stylish hats that meet US military specs are constructed from 65/35 poly-cotton rip-stop with an flexible drawstring chin strap with cardlock for an easy fit",
                "Fitted Short Sleeve Crop Top;$17.99;• Material: 63% rayon, 33% polyester, 4% spandex\n• Machine wash\n\nThis round neck short sleeve crop top will have you feeling wild for it. Keep it trendy and pair it with our flowy ankle length maxi skirt or for a more casual look wear it with our favorite high waisted skinny jeans."
        };
        String xRay3Merchandise[] = {
                "",
                "",
                "",
                "amazon target walmart",
                "amazon target walmart",
                "amazon target walmart",
                "amazon target walmart",
                "amazon target walmart"
        };
        Map<String, List<String>> entry3 = new HashMap<>();
        entry3.put(KEY_TYPE, Arrays.asList(xRay3Type));
        entry3.put(KEY_NAME, Arrays.asList(xRay3Name));
        entry3.put(KEY_IMAGE, Arrays.asList(xRay3ImageUrl));
        entry3.put(KEY_DESCRIPTION, Arrays.asList(xRay3Description));
        entry3.put(KEY_MERCHANDISE, Arrays.asList(xRay3Merchandise));
        listOfItems.add(entry3);




        // Uncharted
        String xRay4Type[] = {
                "0",
                "0",
                "0",
                "1",
                "1",
                "1",
                "1",
                "1"
        };
        String xRay4Name[] = {
                "Tom Holland",
                "Sophia Ali",
                "Mark Wahlberg",
                "Neck Ruffle Fit and Flare Dress",
                "Vegan Leather Moto Jacket",
                "The Boulevard Bomber",
                "Relax Fit Men's Jeans",
                "Uncharted Grey Hoodie"
        };
        String xRay4ImageUrl[] = {
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS_bnTTg0RPTPRfk3n8lHBWB_oJS5SyPYSRTb0ffkdFcdXGNwh-rR7hwqwbykz90m2LWrI&usqp=CAU",
                "https://flxt.tmsimg.com/assets/609607_v9_bb.jpg",
                "https://flxt.tmsimg.com/assets/43431_v9_ba.jpg",
                "https://images.express.com/is/image/expressfashion/0094_07827535_2073_a001?cache=on&wid=960&fmt=jpeg&qlt=85,1&resmode=sharp2&op_usm=1,1,5,0&defaultImage=Photo-Coming-Soon",
                "https://i5.walmartimages.com/asr/07e91197-7e60-4e48-bbeb-510cb2ac420d.bc276ee358e5d9372ac695090fb73a38.jpeg?odnHeight=612&odnWidth=612&odnBg=FFFFFF",
                "https://bonobos-prod-s3.imgix.net/products/286106/original/JACKET_BOMBER-JACKET_BOT01535N1233G_1.jpg?1675112807=&auto=format&fit=clip&cs=srgb&w=768&q=75",
                "https://jcpenney.scene7.com/is/image/JCPenney/beff20c9-7b19-11e8-b7c1-2f0fb63ca7ca?hei=550&wid=550&op_usm=.4%2C.8%2C0%2C0&resmode=sharp2&op_sharpen=1",
                "https://res.cloudinary.com/teepublic/image/private/s--CXeMIyX5--/t_Resized%20Artwork/c_crop,x_10,y_10/c_fit,w_465/c_crop,g_north_west,h_620,w_465,x_0,y_0/g_north_west,u_upload:v1446840653:production:blanks:f6q1psnlmvhpoighmph1,x_-391,y_-276/b_rgb:eeeeee/c_limit,f_auto,h_630,q_90,w_630/v1603477063/production/designs/15352737_0.jpg"
        };
        String xRay4Description[] = {
                "Tom Holland;• Born in Kingston upon Thames, United Kingdom\n• Age: 26 (1996)\n• Height: 5′ 8″\n• Nationality: British;Thomas Stanley Holland is an English actor. His accolades include a British Academy Film Award, three Saturn Awards, a Guinness World Record and an appearance on the Forbes 30 Under 30 Europe list. Some publications have called him one of the most popular actors of his generation.",
                "Sophia Ali;• Born in San Diego, CA, America\n• Age: 27 (1995)\n• Height: 5′ 8″\n• Nationality: American;Sophia Taylor Ramsey Ali is an American actress. She is best known for her work in the MTV romantic comedy series Faking It, the ABC medical drama series Grey's Anatomy, and The Wilds.",
                "Mark Wahlberg;• Born in Boston, MA, America\n• Age: 51 (1971)\n• Height: 5′ 8″\n• Nationality: American;Mark Robert Michael Wahlberg, former stage name Marky Mark, is an American actor, businessman, and former rapper. His work as a leading man spans the comedy, drama, and action genres.",

                "Neck Ruffle Fit and Flare Dress;$128;• Material: 100% polyester/rayon\n• Length: 136cm\n\nThis one features fun ruffle details and a flattering fit and flare style. This piece perfectly pairs with heels or booties for your next event.",
                "Vegan Leather Moto Jacket;$128;• Machine wash cold\n• Tumble dry low\n\nWater resistant outer shell for extra protection against inclement weather.",
                "The Boulevard Bomber;$89;• Material: 60% cotton, 40% nylon\n• Dry clean only\n\nClassic bomber style with rib collar, cuff and hem | Single welt hand pockets with snap | Water repellent | Fully lined.",
                "Relax Fit Men's Jeans;$69.5;• Material: 1% elastane, 99% cotton\n• 5-pocket styling\n• Machine wash cold\n\nA comfortable classic, introduced in 1985 and loved ever since. After the '70s were over, things got more relaxed, including Levi's® jeans. This pair features extra room throughout with a slight, tailor-like taper at the leg.",
                "Uncharted Grey Hoodie;$39;• Material: Cotton/Poly fleece blend\n• Color: Vintage heather\n• Style: Classic hoodie\n\nSuper warm and cozy fleece lining with an adjustable hood and banded cuffs to keep in the heat."
        };
        String xRay4Merchandise[] = {
                "",
                "",
                "",
                "amazon target walmart",
                "amazon costco walmart",
                "amazon target walmart",
                "amazon costco walmart",
                "amazon target walmart"
        };
        Map<String, List<String>> entry4 = new HashMap<>();
        entry4.put(KEY_TYPE, Arrays.asList(xRay4Type));
        entry4.put(KEY_NAME, Arrays.asList(xRay4Name));
        entry4.put(KEY_IMAGE, Arrays.asList(xRay4ImageUrl));
        entry4.put(KEY_DESCRIPTION, Arrays.asList(xRay4Description));
        entry4.put(KEY_MERCHANDISE, Arrays.asList(xRay4Merchandise));
        listOfItems.add(entry4);




        // The Devil Wears Prada
        String xRay5Type[] = {
                "0",
                "0",
                "1",
                "1",
                "1",
                "1",
                "1"
        };
        String xRay5Name[] = {
                "Anne Hathaway",
                "Meryl Streep",
                "Green Leopard Coat",
                "Double Breasted Coat",
                "Wool Alpaca Mohair Coat",
                "Black Wool Trench Coat",
                "Shiny Leather Coat"
        };
        String xRay5ImageUrl[] = {
                "https://cdn.prod.www.spiegel.de/images/bbb54fd1-0001-0004-0000-000000455757_w1200_r1_fpx47.84_fpy44.99.jpg",
                "data:image/jpeg;base64,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AAYAAAAAAAEAMgAAAAEAWgAAAAYAAAAAAAEANQAAAAEALQAAAAYAAAAAAAE4QklNA/gAAAAAAHAAAP////////////////////////////8D6AAAAAD/////////////////////////////A+gAAAAA/////////////////////////////wPoAAAAAP////////////////////////////8D6AAAOEJJTQQGAAAAAAAHAAQAAQABAQA4QklNBAgAAAAAABAAAAABAAACQAAAAkAAAAAAOEJJTQQKAAAAAAABAAA4QklNBAwAAAAAHjQAAAABAAAAiwAAAKAAAAGkAAEGgAAAHhgAGAAB/9j/4gxYSUNDX1BST0ZJTEUAAQEAAAxITGlubwIQAABtbnRyUkdCIFhZWiAHzgACAAkABgAxAABhY3NwTVNGVAAAAABJRUMgc1JHQgAAAAAAAAAAAAAAAAAA9tYAAQAAAADTLUhQICAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABFjcHJ0AAABUAAAADNkZXNjAAABhAAAAGx3dHB0AAAB8AAAABRia3B0AAACBAAAABRyWFlaAAACGAAAABRnWFlaAAACLAAAABRiWFlaAAACQAAAABRkbW5kAAACVAAAAHBkbWRkAAACxAAAAIh2dWVkAAADTAAAAIZ2aWV3AAAD1AAAACRsdW1pAAAD+AAAABRtZWFzAAAEDAAAACR0ZWNoAAAEMAAAAAxyVFJDAAAEPAAACAxnVFJDAAAEPAAACAxiVFJDAAAEPAAACAx0ZXh0AAAAAENvcHlyaWdodCAoYykgMTk5OCBIZXdsZXR0LVBhY2thcmQgQ29tcGFueQAAZGVzYwAAAAAAAAASc1JHQiBJRUM2MTk2Ni0yLjEAAAAAAAAAAAAAABJzUkdCIElFQzYxOTY2LTIuMQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAWFlaIAAAAAAAAPNRAAEAAAABFsxYWVogAAAAAAAAAAAAAAAAAAAAAFhZWiAAAAAAAABvogAAOPUAAAOQWFlaIAAAAAAAAGKZAAC3hQAAGNpYWVogAAAAAAAAJKAAAA+EAAC2z2Rlc2MAAAAAAAAAFklF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bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48eDp4bXBtZXRhIHhtbG5zOng9ImFkb2JlOm5zOm1ldGEvIiB4OnhtcHRrPSJBZG9iZSBYTVAgQ29yZSA1LjUtYzAyMSA3OS4xNTU3NzIsIDIwMTQvMDEvMTMtMTk6NDQ6MDAiPjxyZGY6UkRGIHhtbG5zOnJkZj0iaHR0cDovL3d3dy53My5vcmcvMTk5OS8wMi8yMi1yZGYtc3ludGF4LW5zIyI+PHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiI+PGRjOnRpdGxlIHhtbG5zOmRjPSJodHRwOi8vcHVybC5vcmcvZGMvZWxlbWVudHMvMS4xLyI+ODM5OTAyOTU8L2RjOnRpdGxlPjxkYzpjcmVhdG9yIHhtbG5zOmRjPSJodHRwOi8vcHVybC5vcmcvZGMvZWxlbWVudHMvMS4xLyI+R3JlZ2cgRGVHdWlyZTwvZGM6Y3JlYXRvcj48ZGM6cmlnaHRzIHhtbG5zOmRjPSJodHRwOi8vcHVybC5vcmcvZGMvZWxlbWVudHMvMS4xLyI+MjAxNSBHcmVnZyBEZUd1aXJlPC9kYzpyaWdodHM+PGRjOnN1YmplY3QgeG1sbnM6ZGM9Imh0dHA6Ly9wdXJsLm9yZy9kYy9lbGVtZW50cy8xLjEvIj48cmRmOkJhZz48cmRmOmxpPkNlbGVicml0aWVzPC9yZGY6bGk+PHJkZjpsaT5Bd2FyZDwvcmRmOmxpPjxyZGY6bGk+QXJyaXZhbDwvcmRmOmxpPjxyZGY6bGk+MjAxNTwvcmRmOmxpPjxyZGY6bGk+ODd0aCBBbm51YWwgQWNhZGVteSBBd2FyZHM8L3JkZjpsaT48cmRmOmxpPlJlZCBDYXJwZXQgRXZlbnQ8L3JkZjpsaT48cmRmOmxpPkhvbGx5d29vZCBhbmQgSGlnaGxhbmQgQ2VudGVyPC9yZGY6bGk+PHJkZjpsaT50b3BpY3M8L3JkZjpsaT48cmRmOmxpPnRvcGl4PC9yZGY6bGk+PHJkZjpsaT5iZXN0b2Y8L3JkZjpsaT48cmRmOmxpPnRvcHBpY3M8L3JkZjpsaT48cmRmOmxpPnRvcHBpeDwvcmRmOmxpPjwvcmRmOkJhZz48L2RjOnN1YmplY3Q+PGRjOmRlc2NyaXB0aW9uIHhtbG5zOmRjPSJodHRwOi8vcHVybC5vcmcvZGMvZWxlbWVudHMvMS4xLyI+SE9MTFlXT09ELCBDQSAtIEZFQlJVQVJZIDIyOiBBY3RyZXNzIE1lcnlsIFN0cmVlcCBhcnJpdmVzIGF0IHRoZSA4N3RoIEFubnVhbCBBY2FkZW15IEF3YXJkcyBhdCBIb2xseXdvb2QgJmFtcDsgSGlnaGxhbmQgQ2VudGVyIG9uIEZlYnJ1YXJ5IDIyLCAyMDE1IGluIEhvbGx5d29vZCwgQ2FsaWZvcm5pYS4gIChQaG90byBieSBHcmVnZyBEZUd1aXJlL1dpcmVJbWFnZSk8L2RjOmRlc2NyaXB0aW9uPjwvcmRmOkRlc2NyaXB0aW9uPjxyZGY6RGVzY3JpcHRpb24gcmRmOmFib3V0PSIiPjxwaG90b3Nob3A6QXV0aG9yc1Bvc2l0aW9uIHhtbG5zOnBob3Rvc2hvcD0iaHR0cDovL25zLmFkb2JlLmNvbS9waG90b3Nob3AvMS4wLyI+Q29udHJpYnV0b3I8L3Bob3Rvc2hvcDpBdXRob3JzUG9zaXRpb24+PHBob3Rvc2hvcDpDcmVkaXQgeG1sbnM6cGhvdG9zaG9wPSJodHRwOi8vbnMuYWRvYmUuY29tL3Bob3Rvc2hvcC8xLjAvIj5XaXJlSW1hZ2U8L3Bob3Rvc2hvcDpDcmVkaXQ+PHBob3Rvc2hvcDpTb3VyY2UgeG1sbnM6cGhvdG9zaG9wPSJodHRwOi8vbnMuYWRvYmUuY29tL3Bob3Rvc2hvcC8xLjAvIj5XaXJlSW1hZ2U8L3Bob3Rvc2hvcDpTb3VyY2U+PHBob3Rvc2hvcDpDaXR5IHhtbG5zOnBob3Rvc2hvcD0iaHR0cDovL25zLmFkb2JlLmNvbS9waG90b3Nob3AvMS4wLyI+SG9sbHl3b29kPC9waG90b3Nob3A6Q2l0eT48cGhvdG9zaG9wOlN0YXRlIHhtbG5zOnBob3Rvc2hvcD0iaHR0cDovL25zLmFkb2JlLmNvbS9waG90b3Nob3AvMS4wLyI+Q0E8L3Bob3Rvc2hvcDpTdGF0ZT48cGhvdG9zaG9wOkNvdW50cnkgeG1sbnM6cGhvdG9zaG9wPSJodHRwOi8vbnMuYWRvYmUuY29tL3Bob3Rvc2hvcC8xLjAvIj5Vbml0ZWQgU3RhdGVzPC9waG90b3Nob3A6Q291bnRyeT48cGhvdG9zaG9wOkRhdGVDcmVhdGVkIHhtbG5zOnBob3Rvc2hvcD0iaHR0cDovL25zLmFkb2JlLmNvbS9waG90b3Nob3AvMS4wLyI+MjAxNS0wMi0yMiAwMDowMDowMC4wPC9waG90b3Nob3A6RGF0ZUNyZWF0ZWQ+PHBob3Rvc2hvcDpDYXRlZ29yeSB4bWxuczpwaG90b3Nob3A9Imh0dHA6Ly9ucy5hZG9iZS5jb20vcGhvdG9zaG9wLzEuMC8iPkU8L3Bob3Rvc2hvcDpDYXRlZ29yeT48cGhvdG9zaG9wOlN1cHBsZW1lbnRhbENhdGVnb3JpZXMgeG1sbnM6cGhvdG9zaG9wPSJodHRwOi8vbnMuYWRvYmUuY29tL3Bob3Rvc2hvcC8xLjAvIj48cmRmOkJhZz48cmRmOmxpPkFDRTwvcmRmOmxpPjxyZGY6bGk+Q0VMPC9yZGY6bGk+PHJkZjpsaT5FTlQ8L3JkZjpsaT48cmRmOmxpPlRFTDwvcmRmOmxpPjxyZGY6bGk+QVdEPC9yZGY6bGk+PC9yZGY6QmFnPjwvcGhvdG9zaG9wOlN1cHBsZW1lbnRhbENhdGVnb3JpZXM+PHBob3Rvc2hvcDpIZWFkbGluZSB4bWxuczpwaG90b3Nob3A9Imh0dHA6Ly9ucy5hZG9iZS5jb20vcGhvdG9zaG9wLzEuMC8iPjg3dGggQW5udWFsIEFjYWRlbXkgQXdhcmRzIC0gQXJyaXZhbHM8L3Bob3Rvc2hvcDpIZWFkbGluZT48cGhvdG9zaG9wOkNhcHRpb25Xcml0ZXIgeG1sbnM6cGhvdG9zaG9wPSJodHRwOi8vbnMuYWRvYmUuY29tL3Bob3Rvc2hvcC8xLjAvIj5nZDwvcGhvdG9zaG9wOkNhcHRpb25Xcml0ZXI+PHBob3Rvc2hvcDpJbnN0cnVjdGlvbnMgeG1sbnM6cGhvdG9zaG9wPSJodHRwOi8vbnMuYWRvYmUuY29tL3Bob3Rvc2hvcC8xLjAvIi8+PHBob3Rvc2hvcDpUcmFuc21pc3Npb25SZWZlcmVuY2UgeG1sbnM6cGhvdG9zaG9wPSJodHRwOi8vbnMuYWRvYmUuY29tL3Bob3Rvc2hvcC8xLjAvIj41Mzc2MDUyMDc8L3Bob3Rvc2hvcDpUcmFuc21pc3Npb25SZWZlcmVuY2U+PHBob3Rvc2hvcDpVcmdlbmN5IHhtbG5zOnBob3Rvc2hvcD0iaHR0cDovL25zLmFkb2JlLmNvbS9waG90b3Nob3AvMS4wLyIvPjwvcmRmOkRlc2NyaXB0aW9uPjxyZGY6RGVzY3JpcHRpb24gcmRmOmFib3V0PSIiPjxJcHRjNHhtcENvcmU6TG9jYXRpb24geG1sbnM6SXB0YzR4bXBDb3JlPSJodHRwOi8vaXB0Yy5vcmcvc3RkL0lwdGM0eG1wQ29yZS8xLjAveG1sbnMvIi8+PElwdGM0eG1wQ29yZTpDb3VudHJ5Q29kZSB4bWxuczpJcHRjNHhtcENvcmU9Imh0dHA6Ly9pcHRjLm9yZy9zdGQvSXB0YzR4bXBDb3JlLzEuMC94bWxucy8iPlVTQTwvSXB0YzR4bXBDb3JlOkNvdW50cnlDb2RlPjwvcmRmOkRlc2NyaXB0aW9uPjxyZGY6RGVzY3JpcHRpb24gcmRmOmFib3V0PSIiPjxJcHRjNHhtcEV4dDpQZXJzb25JbkltYWdlIHhtbG5zOklwdGM0eG1wRXh0PSJodHRwOi8vaXB0Yy5vcmcvc3RkL0lwdGM0eG1wRXh0LzIwMDgtMDItMjkvIj48cmRmOkJhZz48cmRmOmxpPk1lcnlsIFN0cmVlcDwvcmRmOmxpPjwvcmRmOkJhZz48L0lwdGM0eG1wRXh0OlBlcnNvbkluSW1hZ2U+PC9yZGY6RGVzY3JpcHRpb24+PHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiI+PEdldHR5SW1hZ2VzR0lGVDpDb21wb3NpdGlvbiB4bWxuczpHZXR0eUltYWdlc0dJRlQ9Imh0dHA6Ly94bXAuZ2V0dHlpbWFnZXMuY29tL2dpZnQvMS4wLyI+PHJkZjpCYWc+PHJkZjpsaT5IZWFkc2hvdDwvcmRmOmxpPjxyZGY6bGk+RXllIENvbnRhY3Q8L3JkZjpsaT48cmRmOmxpPlBvc2VkPC9yZGY6bGk+PC9yZGY6QmFnPjwvR2V0dHlJbWFnZXNHSUZUOkNvbXBvc2l0aW9uPjxHZXR0eUltYWdlc0dJRlQ6SW1hZ2VSYW5rIHhtbG5zOkdldHR5SW1hZ2VzR0lGVD0iaHR0cDovL3htcC5nZXR0eWltYWdlcy5jb20vZ2lmdC8xLjAvIj4xPC9HZXR0eUltYWdlc0dJRlQ6SW1hZ2VSYW5rPjxHZXR0eUltYWdlc0dJRlQ6UGVyc29uYWxpdHkgeG1sbnM6R2V0dHlJbWFnZXNHSUZUPSJodHRwOi8veG1wLmdldHR5aW1hZ2VzLmNvbS9naWZ0LzEuMC8iPjxyZGY6QmFnPjxyZGY6bGk+TWVyeWwgU3RyZWVwPC9yZGY6bGk+PC9yZGY6QmFnPjwvR2V0dHlJbWFnZXNHSUZUOlBlcnNvbmFsaXR5PjxHZXR0eUltYWdlc0dJRlQ6QXNzZXRJRCB4bWxuczpHZXR0eUltYWdlc0dJRlQ9Imh0dHA6Ly94bXAuZ2V0dHlpbWFnZXMuY29tL2dpZnQvMS4wLyI+NDY0MTk2NzYyPC9HZXR0eUltYWdlc0dJRlQ6QXNzZXRJRD48R2V0dHlJbWFnZXNHSUZUOk9yaWdpbmFsQ3JlYXRlRGF0ZVRpbWUgeG1sbnM6R2V0dHlJbWFnZXNHSUZUPSJodHRwOi8veG1wLmdldHR5aW1hZ2VzLmNvbS9naWZ0LzEuMC8iPjIwMTUtMDItMjJUMTg6MTc6MzMtMDg6MDA8L0dldHR5SW1hZ2VzR0lGVDpPcmlnaW5hbENyZWF0ZURhdGVUaW1lPjxHZXR0eUltYWdlc0dJRlQ6Q2FtZXJhTWFrZU1vZGVsIHhtbG5zOkdldHR5SW1hZ2VzR0lGVD0iaHR0cDovL3htcC5nZXR0eWltYWdlcy5jb20vZ2lmdC8xLjAvIj5OSUtPTiBEMzwvR2V0dHlJbWFnZXNHSUZUOkNhbWVyYU1ha2VNb2RlbD48L3JkZjpEZXNjcmlwdGlvbj48L3JkZjpSREY+PC94OnhtcG1ldGE+/9sAhAAJBgcIBwYJCAcICgoJCw0WDw0MDA0bFBUQFiAdIiIgHR8fJCg0LCQmMScfHy09LTE1Nzo6OiMrP0Q/OEM0OTo3AQoKCg0MDRoPDxo3JR8lNzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzf/wAARCAE+AT4DASIAAhEBAxEB/8QAHAAAAgMBAQEBAAAAAAAAAAAAAgMBBAUGAAcI/8QAPRAAAQQBAwIDBgMGBQQDAQAAAQACAxEE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",
                "https://cdn.shopify.com/s/files/1/2505/5036/products/SCV021181-1_1024x1024.jpg?v=1663818425",
                "https://i5.walmartimages.com/asr/4f25fa98-c283-4513-a130-b97df4752a85_1.4ad01b93abbd8d344027a00cc31ce67c.jpeg?odnHeight=612&odnWidth=612&odnBg=FFFFFF",
                "https://b2c-media.maxmara.com/sys-master/m0/MM/2022/2/5016042306/023/s3master/5016042306023-a-tempera_normal.jpg#product",
                "https://www.fanjackets.com/wp-content/uploads/2022/03/Womens-long-wool-coat-double-breasted-black.jpg",
                "https://bottega-veneta.dam.kering.com/m/743220d2534df4b0/Large-713813VKLC02164_A.jpg?v=2"
        };
        String xRay5Description[] = {
                "Anne Hathaway;• Born in New York, NY, America\n• Age: 40 (1982)\n• Height: 5′ 8″\n• Nationality: American;Anne Jacqueline Hathaway is an American actress. The recipient of various accolades, including an Academy Award, a Golden Globe Award, and a Primetime Emmy Award, she was among the world's highest-paid actresses in 2015.",
                "Meryl Streep;• Born in Summit, NJ, America\n• Age: 73 (1949)\n• Height: 5′ 6″\n• Nationality: American;Mary Louise Streep is an American actress. Often described as \"the best actress of her generation\", Streep is particularly known for her versatility and accent adaptability.",

                "Green 1950S Leopard Patchwork Button Coat;$99.99;• Material: 100% polyester\n• Length: Knee-length\n• No Stretch\n\nThe 1950s vintage green button coat is to tribute to the movie The Devil Wears Prado. It can keep your retro feminine charm in cold winter! It's a coat that can change between two collar styles. You can button up all the leopard buttons and the unique leopard neckline that wraps around your neck for warmth and elegance.",
                "Double Breasted Slant Pockets Coat;$82.99;• Material: 100% polyester\n• Button closure\n• Machine wash cold\n\nTake on chilly winter with this stylish midi length coat with its chic stand collar design and double breasted details.",
                "Wool Alpaca Mohair Coat;$871;• Material: 48% wool, 22% alpaca, 15% nylon, 15% mohair\n• Length: 125 cm\n• Do not wash, do not bleach\n\nWool, alpaca and mohair coat featuring wide lapels, kimono sleeves and welt pockets. The central rear vent and waist tie belt enhance the garment’s flared fit.",
                "Black Wool Trench Coat;$179;• Material: 100% wool blend\n• Front: Double breasted button closure\n• Collar: Wide lapel style\n• Pockets: One inside pocket\n• Handwash only\n\nAn elegant addition to your fall wardrobe, this coat is crafted from a blend of premium quality wool. This coat can be worn to work, brunch, or a night out.",
                "Shiny Leather Coat;$6900;• Material: 100% lambskin\n• Half lined\n• Regular fit\n\nCrafted in buttery lambskin, this coat takes styling directions from the classic trench — but with added femininity"
        };
        String xRay5Merchandise[] = {
                "",
                "",
                "amazon target walmart",
                "amazon costco walmart",
                "amazon target walmart",
                "amazon costco walmart",
                "amazon target walmart"
        };
        Map<String, List<String>> entry5 = new HashMap<>();
        entry5.put(KEY_TYPE, Arrays.asList(xRay5Type));
        entry5.put(KEY_NAME, Arrays.asList(xRay5Name));
        entry5.put(KEY_IMAGE, Arrays.asList(xRay5ImageUrl));
        entry5.put(KEY_DESCRIPTION, Arrays.asList(xRay5Description));
        entry5.put(KEY_MERCHANDISE, Arrays.asList(xRay5Merchandise));
        listOfItems.add(entry5);




        // The Wolf of Wall Street
        String xRay6Type[] = {
                "0",
                "0",
                "1",
                "1",
                "1",
                "1",
                "1"
        };
        String xRay6Name[] = {
                "Leonardo DiCaprio",
                "Kyle Chandler",
                "Ralph Lauren Polo Shirt",
                "TAG Heuer Watch",
                "Ray-Ban Sunglasses",
                "Ralph Lauren Polo Jacket",
                "Navy Blue Tie"
        };
        String xRay6ImageUrl[] = {
                "https://cdn.britannica.com/65/227665-050-D74A477E/American-actor-Leonardo-DiCaprio-2016.jpg",
                "https://t1.gstatic.com/licensed-image?q=tbn:ANd9GcSZQi9n4LfoBoYKtwNkFRAiXqyZQdOcNS3moOVtUa5Uz-MB2n6S-l8ewE5LBLkvc9NjqJMOv-2wlaUQvtg",
                "https://www.rlmedia.io/is/image/PoloGSI/s7-1266689_alternate10?$rl_df_pdp_5_7_a10$",
                "https://media.gq-magazine.co.uk/photos/5fa9640bab4311e33c32f637/master/w_1920,h_1280,c_limit/WOLFOFWALLSTREETWATCH_2.jpg",
                "https://images.ray-ban.com/is/image/RayBan/805289391616_0001.png?impolicy=SEO_1x1",
                "https://www.rlmedia.io/is/image/PoloGSI/s7-1337387_alternate10?$rl_df_pdp_5_7_a10$",
                "https://i.etsystatic.com/23311721/r/il/4506a5/2645644622/il_1588xN.2645644622_kle6.jpg"
        };
        String xRay6Description[] = {
                "Leonardo DiCaprio;• Born in Los Angeles, CA, America\n• Age: 48 (1974)\n• Height: 6′ 0″\n• Nationality: American;Leonardo Wilhelm DiCaprio is an American actor and film producer. Known for his work in biographical and period films, he is the recipient of numerous accolades, including an Academy Award, a British Academy Film Award and three Golden Globe Awards.",
                "Kyle Chandler;• Born in Buffalo, NY, America\n• Age: 57 (1965)\n• Height: 6′ 0″\n• Nationality: American;Kyle Martin Chandler is an American actor. Making his screen acting debut in a 1988 television film, Quiet Victory: The Charlie Wedemeyer Story, Chandler's first regular television role was in the ABC drama Homefront.",
                "The Iconic Mesh Polo Shirt;$110;• Material: 100% cotton\n• Length: 28.5\" front, 29.5\" back\n• Machine wash\n\nOver the decades, Ralph Lauren has reimagined his signature style in a wide array of colors and fits, yet all retain the quality and attention to detail of the iconic original. This relaxed version is made with our highly breathable cotton mesh, which offers a textured look and a soft feel. ",
                "TAG Heuer Professional 1000;$3850;• Brand: TAG Heuer\n• Band/Strap: Bracelet\n• Case Material: Gold plated\n• Case Size: 38 mm\n\nFor more than 160 years, TAG Heuer has led, never followed, setting its own rules and driving watchmaking into uncharted territories. Its timepieces are not faithful reflections of tradition, but true representatives of design and engineering at their most innovative and awe-inspiring.",
                "Ray-Ban ® RB4147 Sunglasses;$151;• Frame Shape: Square\n• Frame Color: Polished black\n• Frame Material: Nylon\n• Len Height: 48.5mm\n• Bridge Width: 60 15mm\n\nRay-Ban ® RB4147 sunglasses are fashionable and bold with larger rounded square lenses in propionate plastic frames which offer maximum sun protection while embracing a chic Hollywood look.",
                "Ralph Lauren Polo Soft Wool Oxford Suit Jacket;$698;• Shell Material: Wool\n• Under Collar Material: Mulberry silk\n• Length: 30\" front, 29.5\" back\n• Double vent\n\nThis Polo Soft jacket is tailored from lightweight wool oxford that's both breathable and wrinkle-resistant. Its fit puts a modern spin on classic preppy style with a natural shoulder and a half-canvassed construction.",
                "Navy Blue Tie;$38.5;• Material: 100% silk\n• Blade Width: 7.6 cm\n• Handmade\n\nThis Navy & White Circles Silk Tie is simple but  perfect pair for a navy suit as it compliments rather than clash. This tie is also 100% Macclesfield Silk."
        };
        String xRay6Merchandise[] = {
                "",
                "",
                "target costco walmart",
                "costco amazon target",
                "amazon target walmart",
                "amazon costco walmart",
                "amazon target walmart"
        };
        Map<String, List<String>> entry6 = new HashMap<>();
        entry6.put(KEY_TYPE, Arrays.asList(xRay6Type));
        entry6.put(KEY_NAME, Arrays.asList(xRay6Name));
        entry6.put(KEY_IMAGE, Arrays.asList(xRay6ImageUrl));
        entry6.put(KEY_DESCRIPTION, Arrays.asList(xRay6Description));
        entry6.put(KEY_MERCHANDISE, Arrays.asList(xRay6Merchandise));
        listOfItems.add(entry6);




        // Venom
        String xRay7Type[] = {
                "0",
                "0",
                "1",
                "1",
                "1",
                "1",
                "1"
        };
        String xRay7Name[] = {
                "Tom Hardy",
                "Michelle Williams",

                "Painted Flower Dress",
                "Carry Secret Clutch",
                "Stretch Zip Hoodie",
                "Slub Crew Tee",
                "Dark Slim Straight Jeans"
        };
        String xRay7ImageUrl[] = {
                "https://hips.hearstapps.com/hmg-prod/images/tom-hardy-attends-the-premiere-of-columbia-pictures-venom-news-photo-1587931757.jpg?crop=1.00xw:0.722xh;0,0.00980xh&resize=1200:*",
                "https://image.tmdb.org/t/p/w500/jn3BVMVbIptz2gc6Fhxo1qwJVvW.jpg",
                "https://www.evachic.com/wp-content/uploads/2017/08/Marc-Jacobs-Painted-Flower-Wrap-Dress-9.jpg",
                "https://www.net-a-porter.com/variants/images/46376663162442686/bk/w920_q60.jpg",
                "https://cdn.shopify.com/s/files/1/0021/2602/0666/products/ST-2061_Microfleece_Hoodie_ArmyGreen_A_800x.jpg?v=1642528019",
                "https://americangiant.imgix.net/products/M2-2G-1A-IR_v2070.jpg?v=1652816356&auto=format,compress&w=3000",
                "https://img.hollisterco.com/is/image/anf/KIC_331-6104-0978-276_prod1?policy=product-large"
        };
        String xRay7Description[] = {
                "Tom Hardy;• Born in London, United Kingdom\n• Age: 45 (1977)\n• Height: 5′ 9″\n• Nationality: British;Edward Thomas Hardy CBE is an English actor. After studying acting at the Drama Centre London, he made his film debut in Ridley Scott's Black Hawk Down.",
                "Michelle Williams;• Born in Kalispell, MT, America\n• Age: 42 (1980)\n• Height: 5′ 4″\n• Nationality: American;Michelle Ingrid Williams is an American actress. Known primarily for starring in small-scale independent films with dark or tragic themes, she has received various accolades, including two Golden Globe Awards and a Primetime Emmy Award, in addition to nominations for five Academy Awards and a Tony Award.",

                "Marc Jacobs Painted Flower Wrap Dress;$695;• Brand: Marc Jacobs\n• Material: 100% silk\n• Length: 112 cm\n\nThis Marc Jacobs Painted Flower Wrap Dress is a colorful fashion statement featuring dark florals and a sleek wrap silhouette at midi length. Crafted from finest quality silk, it is easy-to-pack and multi-purpose, especially in vacation time.",
                "Valentino Garavani Carry Secrets Clutch;$2280;• Brand: Valentino Garavani\n• Made in Italy\n\nValentino Garavani's 'Carry Secrets' clutch will keep your treasured possessions safe. Crafted from leather with a structured gold-tone metal frame, it's embellished with countless crystals to depict a dragon motif that was originally showcased in 1969. Let yours be the focal point of an all-black outfit.",
                "Stretch Zip Hoodie;$99;• Material: 100% wool\n• 2 front pockets\n• Machine wash\n\nThe go everywear hoodie now in our exclusive stretch fleece.",
                "Premium Slub Crew Grey Tee;$50;• Material: 100% cotton\n• 6.6 oz./sq. yard\n• Premium slub jersey\n• Machine wash\n\nMade with our substantial and custom premium slub fabric that we spent over a year developing. This fabric is non-see through and non-torquing compared to other fabrics on the market. Rich, varied texture that holds its shape. A polished look that looks great on its own or under a jacket.",
                "Dark Wash Slim Straight Jeans;$49.5;• Material: 95% cotton, 4% polyester, 1% elastane\n• Machine wash\n\nDesigned with a slim fit through the hip and thigh with a 15.5” leg opening, featuring dark wash denim made with Hollister Epic Flex stretch, fading and whiskering, a right coin pocket and iconic back pocket embroidery."
        };
        String xRay7Merchandise[] = {
                "",
                "",
                "target costco walmart",
                "costco amazon target",
                "amazon target walmart",
                "amazon costco walmart",
                "amazon target walmart"
        };
        Map<String, List<String>> entry7 = new HashMap<>();
        entry7.put(KEY_TYPE, Arrays.asList(xRay7Type));
        entry7.put(KEY_NAME, Arrays.asList(xRay7Name));
        entry7.put(KEY_IMAGE, Arrays.asList(xRay7ImageUrl));
        entry7.put(KEY_DESCRIPTION, Arrays.asList(xRay7Description));
        entry7.put(KEY_MERCHANDISE, Arrays.asList(xRay7Merchandise));
        listOfItems.add(entry7);




        // Iron man
        String xRay8Type[] = {
                "0",
                "0",
                "1",
                "1",
                "1",
                "1",
                "1"
        };
        String xRay8Name[] = {
                "Robert Downey Jr",
                "Terrence Howard",

                "Racer Jacket",
                "Painted T-Shirt",
                "Air Force Army Jacket",
                "Bvlgari Diagono Moonphase",
                "Cashmere Crew Neck Sweater",
        };
        String xRay8ImageUrl[] = {
                "https://t2.gstatic.com/licensed-image?q=tbn:ANd9GcQijI6Sf7U-nKAfhHFmFBhVVVIxOfzI3QBeHqEjXJ7qcqbu98eqykx0UdmHrvt9Wx5hKilfZROD0mR85gk",
                "https://t1.gstatic.com/licensed-image?q=tbn:ANd9GcThc7oo4fMr_4pDB10xmnt2EYxddGujH8hxr9Ctr3Y41iEJsme3vpiOdMmkZmufBgOdbLxSxz67nv0GNXI",

                "https://cdn.shopify.com/s/files/1/0419/1525/products/1024x1024-Men-Racer-Black-122122-1_1024x1024.jpg?v=1672325278",
                "https://dd4784dac49866101be5-b98bf53fe71a9e56b6bc9ef824d36d5b.ssl.cf1.rackcdn.com/productImage_0-UJbj4AKZ-zoom.jpg",
                "https://footnycs.com/cdn/shop/products/1d554141-f453-4b02-bef3-3ec3f9673c8a.jpg?v=1671445185&width=493",
                "https://www.watchbrandsdirect.com/cdn/shop/products/DGP42BGLDMP-N_main_large.jpg?v=1571438856",
                "https://www.jcrew.com/s7-img-facade/BE895_GY5980?hei=850&crop=0,0,680,0"
        };
        String xRay8Description[] = {
                "Robert Downey Jr;• Born in New York, NY, America\n• Age: 58 (1965)\n• Height: 5′ 8″\n• Nationality: American;Robert John Downey Jr. is an American actor and producer. His career has been characterized by critical and popular success in his youth, followed by a period of substance abuse and legal troubles, before a resurgence of commercial success later in his career.",
                "Terrence Howard;• Born in Chicago, IL, America\n• Age: 54 (1969)\n• Height: 6′ 0″\n• Nationality: American;Terrence Dashon Howard is an American actor, rapper, singer, and producer. Having his first major roles in the 1995 films Dead Presidents and Mr. Holland's Opus, Howard broke into the mainstream with a succession of television and cinema roles between 2004 and 2006. ",

                "Racer Jacket;$349;• Outer Material: Lambskin leather\n• Inner Material: Polyester interior\n• Dry clean only\n\nThis streamlined leather jacket has been thoughtfully handcrafted to let the quality of the materials speak for themselves. With twin side torso welt pockets, a snap buttoned collar and premium lambskin leather, this represents Thursday versatility at its finest.",
                "Painted Grey T-Shirt;$28;• Brand: Levi's \n• Material: 50% polyester, 25% Rayon, 25% Cotton\n• Machine wash cold\n\nThese grey t-shirts feature a quote by Wendell Berry that reads \"What I Stand For is What I Stand On\"",
                "Air Force Army Jacket;$85.99;• Material: Polyester, cotton\n• Thickness: Standard\n• Detachable Part: Hat Detachable\n• Machine wash\n\nPerfect for a day at the range, smores around the campfire, or training on the toughest terrains.",
                "Bvlgari Diagono Retrograde Moonphase;$19980;• Model: DGP42GMP\n• Movement: Automatic\n• Case Material: 18K rose gold, 42mm\n\nDial: Black with gold batons and Arabic numerals, Strap: Brown crocodile with deployant buckle, Special Features: Moon phase calendar, day, date, second hand",
                "Cashmere Crew neck Sweater;$138;• Material: 100% cashmere\n• Crew neck\n• Hand wash\n\nWe source cashmere that is certified to the AbTF's The Good Cashmere Standard®, which ensures the welfare of the cashmere goats, protects natural resources and improves the working conditions of farmers and farmworkers in Inner Mongolia."
        };
        String xRay8Merchandise[] = {
                "",
                "",
                "amazon target walmart",
                "costco amazon target",
                "amazon target walmart",
                "costco amazon target",
                "amazon target walmart"
        };
        Map<String, List<String>> entry8 = new HashMap<>();
        entry8.put(KEY_TYPE, Arrays.asList(xRay8Type));
        entry8.put(KEY_NAME, Arrays.asList(xRay8Name));
        entry8.put(KEY_IMAGE, Arrays.asList(xRay8ImageUrl));
        entry8.put(KEY_DESCRIPTION, Arrays.asList(xRay8Description));
        entry8.put(KEY_MERCHANDISE, Arrays.asList(xRay8Merchandise));
        listOfItems.add(entry8);




        // Harry Potter and the Prisoner of Azkaban
        String xRay9Type[] = {
                "0",
                "0",
                "1",
                "1",
                "1",
                "1",
                "1"
        };
        String xRay9Name[] = {
                "Daniel Radcliff",
                "Emma Watson",

                "Time Turner™ Necklace",
                "Zip Hooded Sweatshirt Top",
                "Stretch Juniors Jeans",
                "Poly-Tech Jacket",
                "Robertson Heather Ringer Tee"
        };
        String xRay9ImageUrl[] = {
                "https://t3.gstatic.com/licensed-image?q=tbn:ANd9GcTwLzKRSurTa6uHBKBS4Wzu2EEFRcDtl4VTvy7GABg36SFElOlg8rAKbSjMr8PN3u7LU4cOfjWbtjNGTr7JIw97nloYX15l00hqNMjxEhDG",
                "https://t1.gstatic.com/licensed-image?q=tbn:ANd9GcQ5gpvmOl83tBZMpEoS_iC4AJQ0X8l2LuB6wPhBm5e_3L-06OnVgH2Uk_W_gT7R8XF8URSll1CfeClGP2g",

                "https://shop.universalorlando.com/merchimages/p-time-turner-necklace-1349252.jpg",
                "https://i5.walmartimages.com/asr/c249ec41-ea63-4d6d-ac45-0a824a67eeb3.9dc9d8740a644e65cbc73dd078971cc2.jpeg?odnHeight=612&odnWidth=612&odnBg=FFFFFF",
                "https://jcpenney.scene7.com/is/image/JCPenney/DP1122202209035515M?hei=550&wid=550&op_usm=.4%2C.8%2C0%2C0&resmode=sharp2&op_sharpen=1",
                "https://i5.walmartimages.com/asr/2d28a7d6-1e9c-41f2-864f-101de497940e.d0b15b0b19cebb02d346747d3138ff95.jpeg?odnHeight=612&odnWidth=612&odnBg=FFFFFF",
                "https://www.jcrew.com/s7-img-facade/BH180_PP3190?hei=975&crop=0,0,780,0"
        };
        String xRay9Description[] = {
                "Daniel Radcliff;• Born in London, United Kingdom\n• Age: 33 (1989)\n• Height: 5′ 5″\n• Nationality: British;Daniel Jacob Radcliffe is an English actor. He rose to fame at age twelve, when he began portraying Harry Potter in the film series of the same name. Over his career, Radcliffe has received various awards and nominations.",
                "Emma Watson;• Born in Paris, France\n• Age: 32 (1990)\n• Height: 5′ 5″\n• Nationality: British;Emma Charlotte Duerre Watson is an English actress, model and activist. Known for her roles in both blockbusters and independent films, as well as for her women's rights work, she has received a selection of accolades, including a Young Artist Award and three MTV Movie Awards.",

                "Time-Turner™ Necklace;$50;• Length: 16\"\n• Style: Antiqued, gold-tone plated metal\n\nAuthentic replica of Hermione's Time-Turner necklace, as seen in the Harry Potter™ film series. Intricately detailed necklace includes working miniature hourglass, rotating inner rings with etched inscriptions, adjustable chain, and a The Wizarding World of Harry Potter™ drawstring bag for storage.",
                "Zip Hooded Sweatshirt Top;$46.5;• Material: 50% cotton, 50% polyester \n• Metal zipper\n• Pouch pockets\n• Machine wash\n\nUnlined hood with color-matched drawcord; Double-needle stitching at waistband and cuffs; 1x1 rib with spandex; Overlapped fabric across zipper allows full front printing.",
                "Mid-Rise Stretch Juniors Jeans ;$40;• Material: 74% cotton, 24% polyester, 2% spandex\n• Machine wash\n\nContoured waist (no more waist gap!), roomier in the hips and thighs and 5-pocket styling. We are size inclusive, which means – Made for Every Body – Great jeans for all women.",
                "Poly-Tech Full-Zip Track Jacket;$50.63;• Material: 100% polyester fleece\n• Regular fit\n• Machine wash\n\nThis fleece adds a nice touch of cozy warmth on any cold day, wet, windy, or otherwise. Fleece is your plush, warm and dry answer to mother nature’s version of cold liquid sunshine and wind. It’s a super lightweight and convenient piece for all year around.",
                "Robertson Heather Ringer Tee;$12.5;• Material: 52% cotton, 48% polyester\n• Machine wash\n\n"
        };
        String xRay9Merchandise[] = {
                "",
                "",
                "target amazon walmart",
                "amazon target walmart",
                "amazon target walmart",
                "walmart amazon target",
                "amazon target walmart"
        };
        Map<String, List<String>> entry9 = new HashMap<>();
        entry9.put(KEY_TYPE, Arrays.asList(xRay9Type));
        entry9.put(KEY_NAME, Arrays.asList(xRay9Name));
        entry9.put(KEY_IMAGE, Arrays.asList(xRay9ImageUrl));
        entry9.put(KEY_DESCRIPTION, Arrays.asList(xRay9Description));
        entry9.put(KEY_MERCHANDISE, Arrays.asList(xRay9Merchandise));
        listOfItems.add(entry9);




        // Fantastic Beasts and Where to Find Them
        String xRay10Type[] = {
                "0",
                "0",
                "1",
                "1",
                "1",
                "1",
                "1"
        };
        String xRay10Name[] = {
                "Eddie Redmayne",
                "Dan Fogler",

                "Wool Coat",
                "Wand",
                "Vintiquewise Suitcase",
                "Lace Up Boot",
                "Slim Vest Waistcoat"
        };
        String xRay10ImageUrl[] = {
                "https://t1.gstatic.com/licensed-image?q=tbn:ANd9GcRkWTMilN3jaIVZIuzhdkIs5ck2FXH1ACAtaraw051KgS6NJ5l0CCGW5dloGSurSH83gBXs-I2TCav0dLA",
                "https://flxt.tmsimg.com/assets/493325_v9_bd.jpg",

                "https://m.media-amazon.com/images/I/416RRX7XtGL._AC_.jpg",
                "https://shop.universalorlando.com/merchimages/p-interactive-fantastic-beasts-newt-scamander-wand-1346044.jpg",
                "http://t0.gstatic.com/images?q=tbn:ANd9GcQmFRV_NQMWNslzL8mcZOZ0YRoN-TQUoFbngSNcVSgyhBhtJF4Y",
                "https://mobile.yoox.com/images/items/17/17052275RG_14_f.jpg?impolicy=crop&width=387&height=490",
                "https://m.media-amazon.com/images/I/716giK7TpbL._AC_SL1500_.jpg"
        };
        String xRay10Description[] = {
                "Eddie Redmayne;• Born in London, United Kingdom\n• Age: 41 (1982)\n• Height: 6′ 0″\n• Nationality: British;Edward John David Redmayne OBE is an English actor. Known primarily for his role in biopics, he has received various accolades, including an Academy Award, a Tony Award, a BAFTA Award, and two Olivier Awards.",
                "Dan Fogler;• Born in New York, NY, America\n• Age: 46 (1976)\n• Height: 5′ 7″\n• Nationality: American;Daniel Kevin Fogler is an American actor, comedian and writer. He has appeared in films including Balls of Fury, Good Luck Chuck, Fantastic Beasts and Where to Find Them series",

                "Wool Coat;$559.99;• Material: 100% wool\n• Lapel collar\n• 2 side pockets\n• Dry clean only\n\nTransform yourself into the most famous Magizoologist in the history of wizardry with this jacket from Fantastic Beasts And Where To Find Them.",
                "Wand;$39;• Material: Resin\n• Style: Antique\n• Length: 35cm\n\nThe Wand of Newt Scamander in Collector's Box is a meticulous recreation of the wand prop used in Fantastic Beasts and Where to Find Them, crafted with high quality materials. Officially licensed by Warner Bros.",
                "Vintiquewise Old Style Suitcase;$69.51;• Material: Wood, plywood faux leather\n• Size: 13” x 8.5” x 4.5”\n\nOur warm and welcoming steamer trunk brings back days of old time. Remember how excited you are when you were a little kid to look into your grandma's old chest, our decorative trunks will bring back those memories and help you create some new ones too.",
                "Lace Up Boot;$150;• Material: 100% leather\n• Heel Height: 1.25\"\n• Synthetic sole\n\nThe lightly distressed leather upper on this boot from Frye gives it a natural, broken-in-look that's amazingly hip and stylish. A fully laced vamp, a breathable, leather-lined footbed, and stacked wooden heel combine together for a look that is versatile, sophisticated, and charming.",
                "Slim Fit Western Vest Waistcoat;$49.5;• Material: 95% polyester, 5% cotton\n• West cowboy style\n• Slim fit\n\nThis mens suede vest is western cowboy style.It shows the handsomeness and free-and-easy personality of the American west cowboy style. You can pair this suede vest with dress shirt, jacket, jeans, western cowboy hat, stilt felt hat and boots."
        };
        String xRay10Merchandise[] = {
                "",
                "",
                "target costco walmart",
                "costco amazon target",
                "amazon target walmart",
                "amazon costco walmart",
                "amazon target walmart"
        };
        Map<String, List<String>> entry10 = new HashMap<>();
        entry10.put(KEY_TYPE, Arrays.asList(xRay10Type));
        entry10.put(KEY_NAME, Arrays.asList(xRay10Name));
        entry10.put(KEY_IMAGE, Arrays.asList(xRay10ImageUrl));
        entry10.put(KEY_DESCRIPTION, Arrays.asList(xRay10Description));
        entry10.put(KEY_MERCHANDISE, Arrays.asList(xRay10Merchandise));
        listOfItems.add(entry10);




        // Insomnia
        String xRay11Type[] = {
                "0",
                "0",
                "1",
                "1",
                "1",
                "1",
                "1"
        };
        String xRay11Name[] = {
                "Alfredo Pacino",
                "Hilary Swank",

                "TAG HEUER Carrera Watch",
                "Lined Journal Notebook",
                "Modern Classic Suit Jacket",
                "Classic Jackie Cardigan",
                "Bryce Glasses"
        };
        String xRay11ImageUrl[] = {
                "https://pyxis.nymag.com/v1/imgs/bc9/ccb/936534d0b82b77cf0ffbac92010ee38ea3-06-al-pacino.2x.rvertical.w512.jpg",
                "https://encrypted-tbn1.gstatic.com/licensed-image?q=tbn:ANd9GcTLVhxwTlQ9UnDG2AvjQ-rR7Y6QlKr3hweWUmxstYQHMTMPQ9oS2rfrHyP9mvDvP4Adve-Dp6XWova_kyg",

                "https://www.tagheuer.com/on/demandware.static/-/Sites-tagheuer-master/default/dwb6bed08e/TAG_Heuer_Carrera/WBN2111.BA0639/WBN2111.BA0639_0913.png?impolicy=th-pdp-gallery-full",
                "https://m.media-amazon.com/images/I/71YBEL+Kk+L._AC_SX679_.jpg",
                "https://bananarepublicfactory.gapfactory.com/webcontent/0053/909/981/cn53909981.jpg",
                "https://media.kohlsimg.com/is/image/kohls/6304183_Black?wid=805&hei=805&op_sharpen=1",
                "https://img.ebdcdn.com/product/model/portrait/mt6340_m0.jpg?im=Resize,width=500,height=600,aspect=fill;UnsharpMask,sigma=1.0,gain=1.0&q=85"
        };
        String xRay11Description[] = {
                "Alfredo James Pacino;• Born in East Harlem, New York, NY, America\n• Age: 83 (1940)\n• Height: 5′ 6″\n• Nationality: American;Alfredo James Pacino is an American actor. Considered one of the greatest and most influential actors of the 20th century, Pacino has received numerous accolades: including an Academy Award, two Tony Awards, and two Primetime Emmy Awards, making him one of the few performers to have achieved the Triple Crown of Acting.",
                "Hilary Swank;• Born in Lincoln, NE, America\n• Age: 49 (1974)\n• Height: 5′ 6″\n• Nationality: American;Hilary Ann Swank is an American actress and film producer. Swank first became known in 1992 for her role on the television series Camp Wilder and made her film debut with a minor role in Buffy the Vampire Slayer.",

                "TAG HEUER Carrera Date Watch;$3200;• Type: Automatic watch\n• Material: Steel\n• Size: 39 mm\n\nThis TAG Heuer Carrera is ready for anything. Its brushed silver sunray dial is a statement of luxury, beautifully highlighted by the functional and elegant rhodium-plated TAG Heuer logo and Super-Luminova® hands and indexes.",
                "Lined Journal Notebook;$8.99;• Material: Faux leather\n• Size: 5 x 8 inches\n\nThis lined journal notebook features durable and water-resistant vegan leather cover, can protect the pages inside for years and provide a comfortable writing surface.rounded corners, 160 pages, ribbon bookmark & elastic closure band.",
                "Modern Classic Wave Suit Jacket;$150;• Material: 70% wool, 30% polyester\n• Double back vent\n• 3 exterior pockets, 2 interior pockets\n\nDiscover the heritage craftsmanship of this timeless piece with its soft, smooth, blended luxe fabrication and wrinkle-resistant properties.",
                "Classic Jackie Cardigan;$99;• Material: 100% wool\n• Ribbed cuffs\n• Hand wash\n\nStay warm while keeping your style in tact in this women's Croft & Barrow cardigan.",
                "Bryce Glasses;$25;• Material: Metal\n• Size: 53 x 18 x 138 (mm)\n• Weight: 14 g\n• Shape: Rectangle\n\nThe classy, minimalist look of Bryce is perfect for neatening up any outfit. The Silver metal construction of this frame give it a bold and defined look, while the thin temples and lens frames make them lightweight and comfortable."

        };
        String xRay11Merchandise[] = {
                "",
                "",
                "target costco walmart",
                "costco amazon target",
                "amazon target walmart",
                "amazon costco walmart",
                "amazon target walmart"
        };
        Map<String, List<String>> entry11 = new HashMap<>();
        entry11.put(KEY_TYPE, Arrays.asList(xRay11Type));
        entry11.put(KEY_NAME, Arrays.asList(xRay11Name));
        entry11.put(KEY_IMAGE, Arrays.asList(xRay11ImageUrl));
        entry11.put(KEY_DESCRIPTION, Arrays.asList(xRay11Description));
        entry11.put(KEY_MERCHANDISE, Arrays.asList(xRay11Merchandise));
        listOfItems.add(entry11);




        // Fall
        String xRay12Type[] = {
                "0",
                "0",
                "1",
                "1",
                "1",
                "1",
                "1"
        };
        String xRay12Name[] = {
                "Virginia Gardner",
                "Grace Fulton",

                "iPhone SE",
                "DEERC Mini Drone",
                "lululemon High-Rise Pant",
                "Sunchaser UPF Tank",
                "Seamless Ribbed Bodysuit"
        };
        String xRay12ImageUrl[] = {
                "https://www.freewalldownload.com/virginia-gardner/latest-virginia-gardner-actress-hot-hd-wallpaper.jpg",
                "https://t0.gstatic.com/licensed-image?q=tbn:ANd9GcRHj2VLck51dvEOQURFiljqWAgiOfznIYYqCpIJmvbcEv7-CZISeMym1izH2PjpdoEjzQf0pYGyH8UH2Zg",

                "https://t-mobile.scene7.com/is/image/Tmusprod/Apple-iPhone-SE-3rd-gen-Midnight-frontimage",
                "https://m.media-amazon.com/images/I/61Kfx5PPZ4L._AC_SL1100_.jpg",
                "https://images.lululemon.com/is/image/lululemon/LW5FJTS_034204_1?wid=1600&op_usm=0.5,2,10,0&fmt=webp&qlt=80,1&fit=constrain,0&op_sharpen=0&resMode=sharp2&iccEmbed=0&printRes=72",
                "https://athleta.gap.com/webcontent/0029/208/879/cn29208879.jpg",
                "https://www.forever21.com/dw/image/v2/BFKH_PRD/on/demandware.static/-/Sites-f21-master-catalog/default/dw0eeed4bc/1_front_750/00471548-06.jpg?sw=1000&sh=1500"
        };
        String xRay12Description[] = {
                "Virginia Gardner;• Born in Sacramento, CA, America\n• Age: 27 (1995)\n• Height: 5′ 8″\n• Nationality: American;Virginia Elizabeth Gardner is an American actress who played Karolina Dean in the Hulu original series Marvel's Runaways, Vicky in David Gordon Green's horror film Halloween and Hunter Shiloh in Lionsgate's survival film Fall.",
                "Grace Fulton;• Born in American\n• Age: 26 (1996)\n• Height: 5′ 7″\n• Nationality: American;Grace Caroline Currey is an American actress and dancer. She is best known for playing Mary Bromfield in the DC Extended Universe films Shazam! and reprising the role in Shazam! Fury of the Gods. She also joined the The Conjuring Universe in Annabelle: Creation as Carol.",

                "iPhone SE;$479;• Size: 128 GB\n• Color: Midnight\n• Display: 4.7-inch Retina HD display\n• Height:138.4 mm\n• Width: 67.3 mm\n• Depth: 7.3 mm\n• Weight: 144 g\n\n",
                "DEERC Mini Drone;$50;• Model: D20\n• Video Capture Resolution： 720P\n• Item Weight: 69 g\n• Maximum Range: 100 Meters\n\n",
                "lululemon Align™ Ribbed High-Rise Pant;$99;• Length: 25\"\n• Designed for Yoga\n• Buttery-Soft, Ribbed Nulu™ Fabric\n\nThis collection’s great for low-impact workouts like yoga or whenever you want to feel really, really comfortable.",
                "Sunchaser UPF Tank;$59;• Material: 100% polyester\n• Length: 25.75\"\n• UPF 50+ sun protection\n\nFor medium to high impact workouts at the gym, studio, or outdoors",
                "Seamless Ribbed Bodysuit;$14.21;• Material: 92% nylon, 8% spandex\n• Slim-fitted\n• Hand wash cold\n\nA ribbed knit bodysuit featuring a seamless fit and a halter neckline."
        };
        String xRay12Merchandise[] = {
                "",
                "",
                "target costco walmart",
                "costco amazon target",
                "amazon target walmart",
                "amazon costco walmart",
                "amazon target walmart"
        };
        Map<String, List<String>> entry12 = new HashMap<>();
        entry12.put(KEY_TYPE, Arrays.asList(xRay12Type));
        entry12.put(KEY_NAME, Arrays.asList(xRay12Name));
        entry12.put(KEY_IMAGE, Arrays.asList(xRay12ImageUrl));
        entry12.put(KEY_DESCRIPTION, Arrays.asList(xRay12Description));
        entry12.put(KEY_MERCHANDISE, Arrays.asList(xRay12Merchandise));
        listOfItems.add(entry12);




        // Mama Mia
        String xRay13Type[] = {
                "0",
                "0",
                "0",
                "1",
                "1",
                "1",
                "1",
                "1"
        };
        String xRay13Name[] = {
                "Amanda Seyfried",
                "Rachel McDowall",
                "Ashley Lilley",

                "Smiling Eyes Top",
                "The A-Line Denim Short",
                "Printed Floral Shirt",
                "Duke University Shirt",
                "Floral Spiral Notebook"
        };
        String xRay13ImageUrl[] = {
                "https://t0.gstatic.com/licensed-image?q=tbn:ANd9GcTVutBFshbcg-C7GOr6BkZhnbF3eCzgOP3-YY5wvziH8XJaP_8o6T7Zn5rk1z6XcQvm5w1mcFEoS3LwGWU",
                "https://t2.gstatic.com/licensed-image?q=tbn:ANd9GcTH8E9zXz5yArTPZDbXFQLtrzMhP_sYs37nDAhMwi9kBOHcG1EqGjMZz7B3ECQ08_yZoFpydtu3mAdUDIo",
                "https://t0.gstatic.com/licensed-image?q=tbn:ANd9GcSW4da8xMn0EiKpnSdv-P7yZd4lRexlS0ZV31_zn0B4B2yObH5i5-u2Cf8qMqSqsiDD",

                "https://www.lulus.com/images/product/xlarge/2453892_432832.jpg?w=560&hdpi=1",
                "https://media.everlane.com/image/upload/c_fill,w_1200,ar_807:807,q_auto,dpr_1.0,f_auto,fl_progressive:steep/i/6f30f32a_103e",
                "https://litb-cgis.rightinthebox.com/images/640x853/202109/bps/product/inc/feguhm1630918483880.jpg",
                "https://i.etsystatic.com/29249582/r/il/89ed83/3920469229/il_1588xN.3920469229_244f.jpg",
                "https://encrypted-tbn1.gstatic.com/shopping?q=tbn:ANd9GcRvTsG4Xjs8Ni3-T3-jCZbBBOgYvp7welcKqPDNTUokSIL1YYpFE1l0JToU1eDJaJQl7ocEkE1GdJuKt-38Q1oBjwdziK4UIHw7KowobCzf"
        };
        String xRay13Description[] = {
                "Amanda Seyfried;• Born in Allentown, PA, America\n• Age: 37 (1985)\n• Height: 5′ 2″\n• Nationality: American;Amanda Michelle Seyfried is an American actress. Born and raised in Allentown, Pennsylvania, she began modeling at age 11 and ventured into acting at 15, with recurring roles as Lucy Montgomery on the CBS soap opera As the World Turns and Joni Stafford on the ABC soap opera All My Children.",
                "Rachel McDowall;• Born in Liverpool, United Kingdom\n• Age: 38 (1984)\n• Height: 6′ 0″\n• Nationality: British;Rachel Anne McDowall made her film debut as Lisa in the 2008 film Mamma Mia! The Movie, and had her last of eight film or television roles in a 2016 episode of Zoe Ever After. On stage, she played Swedish woman Ulla in Mel Brooks' musical The Producers in the West End and Velma Kelly in productions of the musical Chicago, in the West End and at the Cambridge Theatre in 2008 and the Garrick Theatre in 2012.",
                "Ashley Lilley;• Born in Rothesay, United Kingdom\n• Age: 37 (1986)\n• Height: 5′ 9″\n• Nationality: British;Ashley-Anne Lilley is a Scottish actress and singer. She made her debut in the 2008 film Mamma Mia!",

                "Smiling Eyes Blue and White Embroidered Top;$48;• Material: 100% rayon\n• Unlined\n• Hand wash cold\n\nThe Smiling Eyes Blue and White Embroidered Top is what you need to complete your next Boho ensemble! Gauzy white woven rayon, with blue embroidery, shapes a relaxed bodice with a rounded neckline, front cutout, and tasseled ties. Long sleeves have elastic cuffs.",
                "The A-Line Denim Short;$68;• Material: 100% organic cotton\n• High-rise\n• Zipper fly\n• Machine wash cold\n• Tumble dry low.\n\nThe A-Line Denim Short features a waist-nipping high rise, a zip-fly closure, an easy 5.5\" inseam, and a flattering A-line shape. Plus, it’s made of premium non-stretch organic cotton, which means it has a gutsy feel that will stand the test of time.",
                "Print Green Blue Pink Floral Shirt;$20;• Material: 100% polyester\n• Round neck, long sleeve\n• Machine wash cold\n\n",
                "Duke University shirt;$20;• Material: 100% cotton\n• Light fabric (142 g/m²)\n• Tear away label\n• Handmade\n• Machine wash\n\nThis classic unisex jersey short sleeve tee fits like a well-loved favorite. Soft cotton and quality print make users fall in love with it over and over again.",
                "Floral Spiral Notebook;$12.5;• Size: 5.99\" x 8.25\" (A5)\n• Thickness: 7.34 mm\n• Cover: 0.5 mm\n• 90gsm Paper\n\nSpring Floral Spiral Notebook with dark pink background."
        };
        String xRay13Merchandise[] = {
                "",
                "",
                "",
                "target costco walmart",
                "costco amazon target",
                "amazon target walmart",
                "amazon costco walmart",
                "amazon target walmart"
        };
        Map<String, List<String>> entry13 = new HashMap<>();
        entry13.put(KEY_TYPE, Arrays.asList(xRay13Type));
        entry13.put(KEY_NAME, Arrays.asList(xRay13Name));
        entry13.put(KEY_IMAGE, Arrays.asList(xRay13ImageUrl));
        entry13.put(KEY_DESCRIPTION, Arrays.asList(xRay13Description));
        entry13.put(KEY_MERCHANDISE, Arrays.asList(xRay13Merchandise));
        listOfItems.add(entry13);




        // Lala Land
        String xRay14Type[] = {
                "0",
                "0",
                "1",
                "1",
                "1",
                "1",
                "1"
        };
        String xRay14Name[] = {
                "Emma Stone",
                "Ryan Gosling",

                "Cocktail Floral Party Dress",
                "Mollie Tote",
                "Chelsea satin sandals",
                "The Perfect White Shirt",
                "Leather Tap Shoes"
        };
        String xRay14ImageUrl[] = {
                "https://t1.gstatic.com/licensed-image?q=tbn:ANd9GcQNFLNCZ6Z1gG0oBmIMZ2kZEg1sAiEqZEfjTJNOT3X3YbdHqzCQNpM_JQdjdrljb4Yz-FMdpvhUncoAA2I",
                "https://t3.gstatic.com/licensed-image?q=tbn:ANd9GcQJiEVUzLlly5B3wiUHTfUdys_FZ69ihj61IjZV4hyrnfYpl70NrjNd0Ftvoht_iL72ckAQ0d3EV7K1GG0",

                "https://m.media-amazon.com/images/I/51YSZu-nxtL._AC_UY879_.jpg",
                "https://images.coach.com/is/image/Coach/1671_imp1y_a0?$desktopProduct$",
                "https://cdna.lystit.com/520/650/n/photos/7a3c-2015/12/13/kurt-geiger-london-chelsea-glitter-high-heel-sandals-product-0-702994492-normal.jpeg",
                "https://cdn.shopify.com/s/files/1/0224/9225/products/RT10_White_2035_lores_1400x.jpg?v=1670449052",
                "https://cdn1.discountdance.com/image/1395x1860/ls3004l_1.jpg"
        };
        String xRay14Description[] = {
                "Emma Stone;• Born in Scottsdale, AZ, America\n• Age: 34 (1989)\n• Height: 5′ 6″\n• Nationality: American;Emily Jean \"Emma\" Stone is an American actress. She is the recipient of various accolades, including an Academy Award, a British Academy Film Award, and a Golden Globe Award. In 2017, she was the world's highest-paid actress and named by Time magazine as one of the 100 most influential people in the world.",
                "Ryan Gosling;• Born in London, Canada\n• Age: 42 (1980)\n• Height: 6′ 0″\n• Nationality: Canadian;Ryan Thomas Gosling is a Canadian actor. Prominent in both independent film and major studio features of varying genres, his films have accrued a worldwide box office gross of over 1.9 billion USD.",

                "Cocktail Floral Party Dress;$25.9;• Material: 95% polyester, 5% spandex\n• Elastic closure\n• Featuring square neck\n• Adorable cap sleeves\n• Machine wash\n\n",
                "Mollie Tote;$428;• Material: Double face leather\n• Size: 13.25\" (L) x 11\" (H) x 5\" (W)\n• Handles with 10.25\" drop\n\n\nThis impeccably-crafted tote by Coach features signature chambray and smooth leather, a zip-top closure, fabric lining, side open compartments, and comfortable 4 3/4\" handles with a detachable 22\" strap for crossbody wear.",
                "Kurt Geiger Chelsea satin sandals;$220;• Upper Material: Fabric\n• Lining Material: Leather\n• 95mm-heel sandal\n\nThe undeniably striking Chelsea from Kurt Geiger ticks all the city-girl style boxes. Designed in must-have sleek blue and featuring intricate gold-tone heel tip, this sandal demands attention.",
                "The Perfect White Shirt;$125;• Material: 98% cotton, 2% lycra\n• Spread collar\n• Mitered barrel cuff\n• Stretch\n\nWoven of a cutting-edge, high density luxurious fabric, it eliminates see-through. A touch of lycra lends stretch to make even the longest business trip less tiresome. The no wrinkle, non-iron technology ensures that you always look crisp and pulled together, even when stepping off a plane after many hours aboard. ",
                "Leather Giordano Spectator Tap Shoes;$200;• Material: 100% leather\n• Padded sock liner\n• Cushioned heel bed\n\nThis Giordano Spectator Tap features genuine full-grain leather and Leo's biggest sounding Ultratone screw on taps. Other features include a two-piece leather sole for a flexible arch and a one-half stitched on rubber sole with non-slip traction."
        };
        String xRay14Merchandise[] = {
                "",
                "",
                "target costco walmart",
                "costco amazon target",
                "amazon target walmart",
                "amazon costco walmart",
                "amazon target walmart"
        };
        Map<String, List<String>> entry14 = new HashMap<>();
        entry14.put(KEY_TYPE, Arrays.asList(xRay14Type));
        entry14.put(KEY_NAME, Arrays.asList(xRay14Name));
        entry14.put(KEY_IMAGE, Arrays.asList(xRay14ImageUrl));
        entry14.put(KEY_DESCRIPTION, Arrays.asList(xRay14Description));
        entry14.put(KEY_MERCHANDISE, Arrays.asList(xRay14Merchandise));
        listOfItems.add(entry14);




        // Sherlock Holmes
        String xRay15Type[] = {
                "0",
                "0",
                "1",
                "1",
                "1",
                "1",
                "1"
        };
        String xRay15Name[] = {
                "Robert Downey Jr.",
                "Jude Law",

                "John Bull Top Hat",
                "Double Breasted Wool Coat",
                "Wool Suit",
                "Luther Blend Overcoat",
                "Engrave Handle"
        };
        String xRay15ImageUrl[] = {
                "https://t2.gstatic.com/licensed-image?q=tbn:ANd9GcQijI6Sf7U-nKAfhHFmFBhVVVIxOfzI3QBeHqEjXJ7qcqbu98eqykx0UdmHrvt9Wx5hKilfZROD0mR85gk",
                "https://t3.gstatic.com/licensed-image?q=tbn:ANd9GcT6yLZJDe27K2ZWNIIWZFywSs76i2EfgQhIiWIEs276vI-FuJ1kAbeHNuun4ypXDyWmom9SrhwVCni5-jw",

                "https://m.media-amazon.com/images/I/41G3j9swTPL._SS768_.jpg",
                "https://image.msscdn.net/images/goods_img/20220914/2791379/2791379_16932919445877_500.jpg",
                "https://images.hugoboss.com/is/image/boss/hbna50391090_032_300?$re_fullPageZoom$&qlt=85&fit=crop,1&align=1,1&wid=1600&hei=2424&fmt=webp",
                "https://slimages.macysassets.com/is/image/MCY/products/8/optimized/20006988_fpx.tif?op_sharpen=1&wid=700&hei=855&fit=fit,1&fmt=webp",
                "https://fashionablecanes.com/cdn/shop/products/royal-canes-walking-cane-engraving-chrome-fritz-custom-engrave-handle-walking-cane-16344820809861.jpg?v=1693414279"
        };
        String xRay15Description[] = {
                "Robert Downey Jr;• Born in New York, NY, America\n• Age: 58 (1965)\n• Height: 5′ 8″\n• Nationality: American;Robert John Downey Jr. is an American actor and producer. His career has been characterized by critical and popular success in his youth, followed by a period of substance abuse and legal troubles, before a resurgence of commercial success later in his career.",
                "Jude Law;• Born in London, United Kingdom\n• Age: 50 (1972)\n• Height: 5′ 10″\n• Nationality: British;David Jude Heyworth Law is an English actor. He received a British Academy Film Award, as well as nominations for two Academy Awards, two Tony Awards, and four Golden Globe Awards. In 2007, he received an Honorary César and was named a knight of the Order of Arts and Letters by the French government.",

                "John Bull Top Hat;$89.95;• Material: 100% wool\n• 2.5-inch brim\n• 6.5-inch flared crown\n• 2-inch grosgrain hat band\n• Dry clean only\n\n100 percent wool felt body and smooth leather sweatband makes this the perfect men’s top hat for serious collectors and formal wear lovers.",
                "Double Breasted Long Wool Coat;$229;• Material: 100% wool\n• Double breasted style\n• 2 exterior and 2 inside pockets\n• Chocolate brown color finishing\n\nThis chocolate brown wool coat is constructed from premium quality wool blend and classic fit to give you warmth and style altogether.This coat will keep you cozy during hours of wear on a cold and windy days.",
                "Wool Suit;$895;• Material: 100% wool\n• Fit Back Length: 74 cm\n• Fit Foot Width: 36 cm\n• Notched lapels\n• Welt chest pockets\n\nA two-piece suit created in virgin wool by BOSS Menswear. Offering reliable wearing comfort thanks to the natural stretch in the fabric, this narrow-cut ensemble comprises pants with a hook-and-zipper closure, and a two-button jacket with notch lapels and flap pockets.",
                "Luther Blend Overcoat;$495;• Material: 100% cotton\n• Three-button closure, four-button cuffs\n• Notched lapel\n• Dry clean\n\nAdd a touch of class to your cold-weather look with the classic fit and warm design of this Luther overcoat from Lauren Ralph Lauren.",
                "Chrome Fritz Custom Engrave Handle;$68;• Material: 100% brass handle\n• 3 engraving options\n\nTake personalization to the next level with this gorgeous cane that has an engraving plate right in the handle. With three engraving styles to choose from, this stylish 100% brass handle is chrome plated and has an ornate scroll design that's sure to wow all who see it."
        };
        String xRay15Merchandise[] = {
                "",
                "",
                "target costco walmart",
                "costco amazon target",
                "amazon target walmart",
                "amazon costco walmart",
                "amazon target walmart"
        };
        Map<String, List<String>> entry15 = new HashMap<>();
        entry15.put(KEY_TYPE, Arrays.asList(xRay15Type));
        entry15.put(KEY_NAME, Arrays.asList(xRay15Name));
        entry15.put(KEY_IMAGE, Arrays.asList(xRay15ImageUrl));
        entry15.put(KEY_DESCRIPTION, Arrays.asList(xRay15Description));
        entry15.put(KEY_MERCHANDISE, Arrays.asList(xRay15Merchandise));
        listOfItems.add(entry15);





        // The Da Vinci Code
        String xRay16Type[] = {
                "0",
                "0",
                "1",
                "1",
                "1",
                "1",
                "1"
        };
        String xRay16Name[] = {
                "Tom Hanks",
                "Audrey Tautou",

                "LED UV Flashlight",
                "The Relaxed Oxford Shirt",
                "Lauren Conrad Blazer",
                "French Dress Shirt",
                "Classic Fit Suit"
        };
        String xRay16ImageUrl[] = {
                "https://hips.hearstapps.com/hmg-prod/images/gettyimages-1257937597.jpg?resize=1200:*",
                "https://m.media-amazon.com/images/M/MV5BMTYzOTgyNjk1Nl5BMl5BanBnXkFtZTcwNjMwMjI1OQ@@._V1_.jpg",

                "https://i5.walmartimages.com/asr/6d522a53-a8ed-4e97-9bdf-dee3b23e4d09.87bb462f27c6133ab8ce3adea82e579c.jpeg?odnHeight=612&odnWidth=612&odnBg=FFFFFF",
                "https://media.everlane.com/image/upload/c_fill,w_828,ar_1:1,q_auto,dpr_1.0,f_auto,fl_progressive:steep/i/c91d1081_64c7",
                "https://media.kohlsimg.com/is/image/kohls/3633856_Dull_Black?wid=600&hei=600&op_sharpen=1",
                "https://s7images.paulfredrick.com/is/image/PaulFredrick/DT3503_BLK?$SH_PDPmain_xl$",
                "https://image.josbank.com/is/image/JosBank/33RF_01_1905_BLACK_MAIN?$JABPDPSharpen$&hei=1080&wid=800&align=0,-1"
        };
        String xRay16Description[] = {
                "Tom Hanks;• Born in Concord, CA, America\n• Age: 66 (1956)\n• Height: 6′ 0″\n• Nationality: American;Thomas Jeffrey Hanks is an American actor and filmmaker. Known for both his comedic and dramatic roles, he is one of the most popular and recognizable film stars worldwide, and is regarded as an American cultural icon.",
                "Audrey Tautou;• Born in Beaumont, France\n• Age: 46 (1976)\n• Height: 5′ 3″\n• Nationality: French;Audrey Justine Tautou is a French actress. She made her acting debut at the age of 18 on television and her feature film debut in Venus Beauty Institute, for which she received critical acclaim and won the César Award for Most Promising Actress.",

                "LED UV Flashlight;$8.99;• Material: Aluminum\n• Power Source: Battery powered\n• Dimensions: 1.2\" (D) x 1.08\" (W) x 3.74\" (H)\n\nVansky UV flashlight black light is Perfect for Revealing Dry Dog, Cat and Rodent Stains and smudgy Spots that Couldn't See With the Naked Eye. Easily Spot Scorpions, Authenticate currency,Official Identification cards,Passports.The black light can also act as Nail Dryer.",
                "The Relaxed Oxford Shirt;$88;• Material: 100% organic cotton\n• Relaxed fit\n• Machine wash cold\n• Tumble dry low\n\nThis style uses organic cotton. Organic cotton is better for the soil and water, and it’s safer for the workers.",
                "Lauren Conrad Fitted Blazer;$58;• Material: 95% polyester, 3% rayon, 2% spandex\n• Button front\n• Rolled 3/4-length sleeves\n• Machine wash\n\nWhether you dress it up or down, this women's LC Lauren Conrad blazer is a charming choice.",
                "French Dress Shirt;$95;• Material: 100% non-iron 2-ply cotton\n• Straight collar\n• French cuffs\n• Break resistant buttons\n• Dry clean only\n\nCut generously for a fit that offers maximum comfort.",
                "Classic Fit Suit Separates Tuxedo Coat;$334.99;• Material: 100% wool\n• 2 buttons\n• Satin notch lapel\n• Dry clean only\n\nCreate a memorable formal look with this fine wool tuxedo coat from Lauren By Ralph Lauren. With suit separates you can choose coat, pant, and/or vest sizes separately for an ideal fit. This fine coat features 100% wool fabric woven with natural stretch for the best fit and comfort."
        };
        String xRay16Merchandise[] = {
                "",
                "",
                "target costco walmart",
                "costco amazon target",
                "amazon target walmart",
                "amazon costco walmart",
                "amazon target walmart"
        };
        Map<String, List<String>> entry16 = new HashMap<>();
        entry16.put(KEY_TYPE, Arrays.asList(xRay16Type));
        entry16.put(KEY_NAME, Arrays.asList(xRay16Name));
        entry16.put(KEY_IMAGE, Arrays.asList(xRay16ImageUrl));
        entry16.put(KEY_DESCRIPTION, Arrays.asList(xRay16Description));
        entry16.put(KEY_MERCHANDISE, Arrays.asList(xRay16Merchandise));
        listOfItems.add(entry16);




        // Flipped
        String xRay17Type[] = {
                "0",
                "0",
                "1",
                "1",
                "1",
                "1",
                "1"
        };
        String xRay17Name[] = {
                "Madeline Carroll",
                "Callan McAuliffe",

                "Button Knit Cardigan",
                "School Uniform Shirt",
                "A-Line Vintage Skirt",
                "Striped Knit Oxford Shirt",
                "Flex Taper Pants"
        };
        String xRay17ImageUrl[] = {
                "https://t1.gstatic.com/licensed-image?q=tbn:ANd9GcS9JKZ7KYxtTjVPaSeFkDtI0BR_jrtlbhNUdqOSjSdoMbW27NQy7VUriA8CefTkbV_NXK5_fUuCWU3Bss8",
                "https://media1.popsugar-assets.com/files/thumbor/c7lZbPtG9q7QBf_n-vPyvjNd76c/fit-in/2048xorig/filters:format_auto-!!-:strip_icc-!!-/2019/05/17/103/n/44701584/bd6b6eb5bb8bf2d7_GettyImages-1125246559/i/Callan-McAuliffe-Taylor.jpg",

                "https://st.mngbcn.com/rcs/pics/static/T3/fotos/S20/37054377_16_B.jpg?ts=1662386484065&imwidth=571&imdensity=2",
                "https://cdn.shopify.com/s/files/1/0268/0854/9559/products/6803073710003070_001.jpg?v=1666642827&width=1944",
                "https://m.media-amazon.com/images/I/81hWZqlOscL._AC_SL1500_.jpg",
                "https://www.rlmedia.io/is/image/PoloGSI/s7-1253259_lifestyle?$rl_df_pdp_5_7_lif$",
                "https://oldnavy.gap.com/webcontent/0050/540/273/cn50540273.jpg"
        };
        String xRay17Description[] = {
                "Madeline Carroll;• Born in Los Angeles, CA, America\n• Age: 27 (1996)\n• Height: 5′ 4″\n• Nationality: American;Madeline Carroll is an American actress known for starring as Juli Baker in Flipped, as Molly Johnson in Swing Vote, as Farren in The Spy Next Door, and as Willow O'Neil in The Magic of Belle Isle.",
                "Callan McAuliffe;• Born in Clontarf, Australia\n• Age: 28 (1995)\n• Height: 5′ 10″\n• Nationality: Australian;Callan Ryan Claude McAuliffe is an Australian actor, known for his roles as Bryce Loski in Flipped and Sam Goode in I Am Number Four. He appeared as young Jay Gatsby in the 2013 film The Great Gatsby. From 2017 to 2022 he starred on The Walking Dead as Alden.",

                "Button Knit Cardigan;$39.99;• Material: 90% polyester,10% polyamide\n• Knitted braided fabric\n• V-neck\n• Long sleeve\n• Button up\n\nRecycled polyester is obtained from polyester textile waste, marine litter or PET plastic bottles that are used to produce new fabric.",
                "School Uniform Yellow Long Sleeve Shirt;$9.99;• Material: 60% cotton, 40% polyester\n• Polo collar\n• Machine wash cold\n\nTopped with a flat-knit collar and three-button placket, this polo shirt is crafted in organic cotton with a bubble structure.",
                "A-Line Vintage Skirt;$45;• Material: 50% cotton, 50% polyester\n• 2 pockets\n• A-line silhouette\n• Classic plaid pattern\n\nBrown and multicolor vintage knee-length pleated skirt featuring plaid pattern throughout, tonal stitching and concealed hook-and-eye closure at waist front.",
                "Striped Knit Oxford Shirt;$69.99;• Material: 100% cotton\n• Slim fit\n• Button-down point collar\n• Machine wash\n\nAlthough it takes its name from England's most famous academic institution, the oxford shirt has its origins on the backs of early British polo players, who wore shirts made from oxford cloth on the field and used pins to keep their collars in place during rigorous play.",
                "Flex Taper Pants;$18;• Material: 98% cotton, 2% spandex\n• Elasticized comfort waistband\n• Machine wash\n\nWith an easy elasticized waistband, OGC pants are the ultimate combo of comfort chops & style cred. Dressier than sweats, relaxier than dress pants"
        };
        String xRay17Merchandise[] = {
                "",
                "",
                "target costco walmart",
                "costco amazon target",
                "amazon target walmart",
                "amazon costco walmart",
                "amazon target walmart"
        };
        Map<String, List<String>> entry17 = new HashMap<>();
        entry17.put(KEY_TYPE, Arrays.asList(xRay17Type));
        entry17.put(KEY_NAME, Arrays.asList(xRay17Name));
        entry17.put(KEY_IMAGE, Arrays.asList(xRay17ImageUrl));
        entry17.put(KEY_DESCRIPTION, Arrays.asList(xRay17Description));
        entry17.put(KEY_MERCHANDISE, Arrays.asList(xRay17Merchandise));
        listOfItems.add(entry17);




        // Crazy Rich Asians
        String xRay18Type[] = {
                "0",
                "0",
                "0",
                "1",
                "1",
                "1",
                "1",
                "1"
        };
        String xRay18Name[] = {
                "Constance Wu",
                "Henry Golding",
                "Awkwafina",

                "Lurex Maxi Dress",
                "Jil Sander Disc Earrings",
                "Audi R8 Coupe",
                "Delave Sport Coat",
                "Stripe-Trim Silk Blouse"
        };
        String xRay18ImageUrl[] = {
                "https://de.web.img3.acsta.net/pictures/19/10/15/22/54/2715291.jpg",
                "https://t1.gstatic.com/licensed-image?q=tbn:ANd9GcQSmEFRqQUT3_8mkOWGU_D_PcvwZ_bBr72rw83NXEyXL62xU6dxwwwz0tlwqQ5trmAUT5UzaHkA_j7-RuI",
                "https://t0.gstatic.com/licensed-image?q=tbn:ANd9GcQ6V4ut50RTK_XFCW80J1VMN3KDZGjrIk8hGvHp_EhMUfh_howo1KxoUed9xCI5qgra4Uo9QnEO94xZXe8",

                "https://i.redd.it/l0k4kqbocwo71.jpg",
                "https://www.ekseption.com/media/catalog/product/cache/2d278355bb18eedbbf016ac97d4c613d/2/7/270296-1.jpg",
                "https://mediaservice.audi.com/media/fast/H4sIAAAAAAAAAFvzloG1tIiBOTrayfuvpGh6-m1zJgaGigIGBgZGoDhTtNOaz-I_2DhCHkCFGWczsJSnJhUwAlW4MXFl5iamp-qDBPgZuYssdFMrSnTzUnPz2YHS3DxTwqcXLEttz73ur_z5F8MBzRVM4isYeLoDKnzuLZnAKLYny4zvRNfl_Yv25THw7GTq5Fv-qvrrVdl_99t1A3-eLzPhYeCZlffhqr27uHRWj2VoXeu0Le-UOjcw8PROW_q3p-TY8dSwqY-TXwdu_PTp-noGnk1Lf_bONn3u8uhcI_fq6ZbdCkL_cxl45FnOXlGZmTn51MovZa2RKpGsYob3GHgOqui3-c9YXVMbxLDk47QrqTn73fYy8OytUPzNEmchcPPJicudf0_Kpl-3EGTg6TmnLzm983uWZ-CMm6UFqyaZfGq_wcATL1pr0rB1a2SGWHM_IwvLFPWirgMMrMBgYfkNIiKBBLcgkOBQYwCTQIK5HRRqm0F85nBmoLwDkNHCAAJ8wqVFOQWJRYm5ekWpxQX5ecWZZamCGgZEAmFWH8dI1yAABmAHuM4BAAA?wid=2388",
                "https://cdn.shopify.com/s/files/1/0021/6229/4829/products/Delave-Linen-Sport-Coat-Brilliant-White-Cubavera-Collection_1080x.jpg?v=1631839644",
                "https://images.bergdorfgoodman.com/ca/1/product_assets/W/0/F/X/G/BGW0FXG_mu.jpg"
        };
        String xRay18Description[] = {
                "Constance Wu;• Born in Richmond, VA, America\n• Age: 41 (1982)\n• Height: 5′ 1″\n• Nationality: American;Constance Wu is an American actress. Wu was included on Time magazine's list of the 100 most influential people in the world in 2017. She has earned several accolades, including nominations for a Golden Globe Award, four Critics' Choice Awards, a Screen Actors Guild Award, and two TCA Awards.",
                "Henry Golding;• Born in Betong, Malaysia\n• Age: 36 (1987)\n• Height: 6′ 1″\n• Nationality: British, Malaysian;Henry Ewan Golding is a Malaysian and British actor and television host. Golding has been a presenter on BBC's The Travel Show since 2014.",
                "Awkwafina;• Born in Stony Brook, NY, America\n• Age: 34 (1988)\n• Height: 5′ 1″\n• Nationality: American;Nora Lum, known professionally as Awkwafina, is an American actress, rapper, and comedian who rose to prominence in 2012 when her rap song \"My Vag\" became popular on YouTube. She then released her debut album, Yellow Ranger, and appeared on the MTV comedy series Girl Code.",

                "Lurex Maxi Dress;$995;• Brand: M Missoni\n• Material: 100% silk\n• V-neck\n• Sleeveless\n• Specialist cleaning\n\nSensual maxi dress detailed with faded colour striped pattern embellished with all-over gold tone lurex inserts and detailed with double spaghetti strap creating a criss cross effect at back, fitted high waist and flared skirt",
                "Jil Sander Disc Earrings;$532;• Material: 100% 925 silver\n• Diameter: 6 cm\n\nJil Sander pair of disc earrings in in golden silver featuring engraved logo and snap closure. For pierced ears only. Antioxidant treatment and nickel tested.",
                "Audi R8 Coupe;$158600;• Model: 562 hp V10\n• 20\" 5-V-spoke-evo design wheels\n• Sport suspension for RWD\n• Audi virtual cockpit with Sport mode\n• 18-way power seats\n\n",
                "Delave Linen Sport Coat;$225;• Material: 100% linen\n• Tailored fit\n• Notched collar\n• Boat-shaped chest pockets\n\nA smart and timeless tailored piece, this lightweight men’s sport coat is cut from 100% Delave linen - a top tier linen with a high-low heathered look and unique texture.",
                "Stripe-Trim Dog-Print Silk Blouse;$49.5;• Material: 100% silk\n• Spread collar, hidden placket\n• Long sleeves, button cuffs\n• Relaxed silhouette\n\nStella McCartney dog-print blouse with striped collar, sleeve, and cuff."
        };
        String xRay18Merchandise[] = {
                "",
                "",
                "",
                "target costco walmart",
                "costco amazon target",
                "costco target amazon",
                "amazon costco walmart",
                "amazon target walmart"
        };
        Map<String, List<String>> entry18 = new HashMap<>();
        entry18.put(KEY_TYPE, Arrays.asList(xRay18Type));
        entry18.put(KEY_NAME, Arrays.asList(xRay18Name));
        entry18.put(KEY_IMAGE, Arrays.asList(xRay18ImageUrl));
        entry18.put(KEY_DESCRIPTION, Arrays.asList(xRay18Description));
        entry18.put(KEY_MERCHANDISE, Arrays.asList(xRay18Merchandise));
        listOfItems.add(entry18);




        // Inception
        String xRay19Type[] = {
                "0",
                "0",
                "1",
                "1",
                "1",
                "1",
                "1"
        };
        String xRay19Name[] = {
                "Leonardo DiCaprio",
                "Elliot Page",

                "British Style Tea Set",
                "Button-Front Cardigan",
                "Stand Collar Silk Blouse",
                "Oxford Shirt",
                "Roughneck Chore Jacket"
        };
        String xRay19ImageUrl[] = {
                "https://cdn.britannica.com/65/227665-050-D74A477E/American-actor-Leonardo-DiCaprio-2016.jpg",
                "https://m.media-amazon.com/images/M/MV5BNmNhZmFjM2ItNTlkNi00ZTQ4LTk3NzYtYTgwNTJiMTg4OWQzXkEyXkFqcGdeQXVyMTM1MjAxMDc3._V1_FMjpg_UX1000_.jpg",

                "https://m.media-amazon.com/images/I/A1EWEloIIAL._AC_SX679_.jpg",
                "https://oldnavy.gap.com/webcontent/0018/874/657/cn18874657.jpg",
                "https://www.lilysilk.com/media/catalog/product/m1/8783/silver/stand-krave-lange-a-ermer-silke-bluse-silver-xs-01.jpg?quality=90&bg-color=255,255,255&fit=bounds&height=&width=&canvas=:",
                "https://images.lululemon.com/is/image/lululemon/LM3COWS_054238_1?wid=1280&op_usm=0.5,2,10,0&fmt=webp&qlt=80,1&fit=constrain,0&op_sharpen=0&resMode=sharp2&iccEmbed=0&printRes=72",
                "https://americangiant.imgix.net/products/M4-3H-7-VIN_7369.jpg?v=1658253598&auto=format,compress&w=1592"
        };
        String xRay19Description[] = {
                "Leonardo DiCaprio;• Born in Los Angeles, CA, America\n• Age: 48 (1974)\n• Height: 6′ 0″\n• Nationality: American;Leonardo Wilhelm DiCaprio is an American actor and film producer. Known for his work in biographical and period films, he is the recipient of numerous accolades, including an Academy Award, a British Academy Film Award and three Golden Globe Awards.",
                "Elliot Page;• Born in Municipality, Canada\n• Age: 36 (1987)\n• Height: 5′ 1″\n• Nationality: Canadian;Elliot Page is a Canadian actor. He has received various accolades, including an Academy Award nomination, two BAFTA Awards and Primetime Emmy Award nominations, and a Satellite Award. Page was assigned female at birth, and later publicly came out as a trans man in December 2020.",

                "British Style Tea Set;$24.99;• Set Of 6 Fine White Porcelain Cups & Saucers Set\n• Fine And High Quality Lead -Free Porcelain\n• Dishwasher, and microwave safe\n\n",
                "Button-Front Cardigan;$99;• Material: 100% cotton\n• Crew neck\n• Long sleeves\n• Machine wash cold\n\nThis comfortable, casual, elegant and classy cardigan sweater comes in multiple colors, making it a perfect fit for any occasion. This soft fitted knit cardigan sweaters are made out of high quality material.",
                "Stand Collar Long Sleeves Silk Blouse;$129;• Material: 90% mulberry silk, 10% spandex\n• Regular fit\n\nMade from light and soft, breathable silk fabric with elasticity, this blouse will seamlessly become part of your everyday wardrobe and will guarantee you comfort without even noticing it.",
                "Commission Long-Sleeve Oxford Shirt;$118;• Material: 95% cotton, 4% polyester\n• Four-way stretch fabric\n• Classic fit\n• Machine wash\n\nThis garment was treated with our No-Stink Zinc™ technology to inhibit the growth of odour-causing bacteria on the fabric.",
                "Roughneck Chore Jacket;$200;• Material: 98% cotton, 2% spandex\n• 9.5 oz/sq yd\n• Garment dye\n• Machine wash cold\n\nThe same rugged canvas in our best-selling Roughneck Pant, now in a timeless and versatile chore jacket. Features an unlined, durable canvas cotton shell for lightweight layering and pocket detailing for easy storage.",
        };
        String xRay19Merchandise[] = {
                "",
                "",
                "target costco walmart",
                "costco amazon target",
                "amazon target walmart",
                "amazon costco walmart",
                "amazon target walmart"
        };
        Map<String, List<String>> entry19 = new HashMap<>();
        entry19.put(KEY_TYPE, Arrays.asList(xRay19Type));
        entry19.put(KEY_NAME, Arrays.asList(xRay19Name));
        entry19.put(KEY_IMAGE, Arrays.asList(xRay19ImageUrl));
        entry19.put(KEY_DESCRIPTION, Arrays.asList(xRay19Description));
        entry19.put(KEY_MERCHANDISE, Arrays.asList(xRay19Merchandise));
        listOfItems.add(entry19);




        // The Adam Project
        String xRay20Type[] = {
                "0",
                "0",
                "1",
                "1",
                "1",
                "1",
                "1"
        };
        String xRay20Name[] = {
                "Ryan Reynolds",
                "Walker Scobell",

                "Tactical Flashlights Torch",
                "Deluxe Blended Coveralls",
                "Vest Gilet",
                "Flannel Plaid Casual Shirt",
                "Stainless Photo Necklace"
        };
        String xRay20ImageUrl[] = {
                "https://t2.gstatic.com/licensed-image?q=tbn:ANd9GcRDIJ-PkZ_U6WOLo7fm-UVwSZbyFb8fu5TVmvvErBzsxb_TmMMdJEyOMBT53C7zqxvuqDATtVONl_l5zPM",
                "https://m.media-amazon.com/images/M/MV5BYjVjYTQ4MDYtYzYxOC00NGYyLWE0YTAtM2Q2YjViYmI4ZTg5XkEyXkFqcGdeQXVyMTI0MDc3MjA0._V1_.jpg",

                "https://m.media-amazon.com/images/I/71ZUoJVwf4L._AC_SX679_PIbundle-2,TopRight,0,0_SH20_.jpg",
                "https://i5.walmartimages.com/asr/e4c25301-5516-4dde-8fa2-ee0ae429daf7_1.23520519ba84b36b1388104edb1b376d.jpeg?odnHeight=612&odnWidth=612&odnBg=FFFFFF",
                "https://litb-cgis.rightinthebox.com/images/640x640/202208/bps/product/inc/pxehak1659347813002.jpg",
                "https://m.media-amazon.com/images/I/71gEorVExpL._AC_UX679_.jpg",
                "https://cdn.shopify.com/s/files/1/0266/8842/1959/products/NL11476_2229b390-deb8-40c5-a7ea-3402ae30d54b_650x650.jpg?v=1681760653"
        };
        String xRay20Description[] = {
                "Ryan Reynolds;• Born in Vancouver, Canada\n• Age: 46 (1976)\n• Height: 6′ 2″\n• Nationality: American, Canadian;Ryan Rodney Reynolds began his career starring in the Canadian teen soap opera Hillside, and had minor roles before landing the lead role on the sitcom Two Guys and a Girl between 1998 and 2001.",
                "Walker Scobell;• Born in Los Angeles, CA, America\n• Age: 14 (2009)\n• Height: 4′ 11″\n• Nationality: American;Walker Scobell is best recognized for his role in the sci-fi film The Adam Project. He also appeared in the film Secret Headquarters in the same year. He began acting while in elementary school.",

                "Tactical Flashlights Torch;$14.99;• Super bright with 5 Modes\n• Adjustable focus\n• Waterproof\n\nWidely use-easy to carry with you as a backup",
                "Deluxe Blended Coveralls;$54.99;• Material: 65% polyester, 35% cotton\n• Zipper: Double-ended\n\nThese men's long sleeve coveralls are spacious enough to layer underneath and are comfortable enough to wear during long, tough days. Put them on to work on vehicles and other tasks.",
                "Vest Gilet;$55.98;• Material: 100% polyester\n• Micro-elastic\n• Regular fit\n• Machine wash cold\n\nOutdoor work fishing travel photo vest perfect for casual daily wear and outdoor activities, such as traveling, safari, cycling, camping, hiking, sports, working, fishing, photography, sightseeing, hunting, bird watching, river guide adventures, and any other activities.",
                "Flannel Plaid Casual Shirt;$18.49;• Material: 100% cotton\n• Flannel\n• Button closure\n• Machine wash\n\nClassically styled with a pointed collar and button front, long sleeve, chest pocket, curved shirttail hem, suitable for both boys and girls from little, big to youth.",
                "Stainless Photo Necklace;$69.3;• Length: 20\"\n• Custom photo pendant\n• Engraving options\n\nCreate a unique dog tag photo necklace with Eve's Addiction's new photo jewelry. This stunning Stainless Photo Necklace can be customized in seconds with your own photo!"
        };
        String xRay20Merchandise[] = {
                "",
                "",
                "target costco walmart",
                "walmart amazon target",
                "amazon target walmart",
                "amazon costco walmart",
                "amazon target walmart"
        };
        Map<String, List<String>> entry20 = new HashMap<>();
        entry20.put(KEY_TYPE, Arrays.asList(xRay20Type));
        entry20.put(KEY_NAME, Arrays.asList(xRay20Name));
        entry20.put(KEY_IMAGE, Arrays.asList(xRay20ImageUrl));
        entry20.put(KEY_DESCRIPTION, Arrays.asList(xRay20Description));
        entry20.put(KEY_MERCHANDISE, Arrays.asList(xRay20Merchandise));
        listOfItems.add(entry20);




        // Space Jam
        String xRay21Type[] = {
                "0",
                "0",
                "1",
                "1",
                "1",
                "1",
                "1"
        };
        String xRay21Name[] = {
                "Michael Jordan",
                "Wayne Knight",

                "Basketball",
                "Jordan Grey Tank",
                "Jordan Brooklyn Fleece",
                "Air Jordan 9 OG",
                "Morrow Glasses"
        };
        String xRay21ImageUrl[] = {
                "https://upload.wikimedia.org/wikipedia/commons/a/ae/Michael_Jordan_in_2014.jpg",
                "https://static.wikia.nocookie.net/jurassicpark/images/d/d0/Wayne_Knight.png/revision/latest?cb=20190112092853",

                "https://target.scene7.com/is/image/Target/GUEST_20affc7e-e0d7-4eb6-a6f3-68d13520f8be?wid=1200&hei=1200&qlt=80&fmt=webp",
                "https://i5.walmartimages.com/seo/Air-Jordan-Men-s-All-Season-Compression-Tank-Grey-Black-642349-091_dbe4f903-6453-47ea-bc48-0fdb7e13c33b.0fdfae6d251319bd0effd634d132ea68.jpeg?odnHeight=640&odnWidth=640&odnBg=FFFFFF",
                "https://static.nike.com/a/images/c_limit,w_592,f_auto/t_product_v1/u_126ab356-44d8-4a06-89b4-fcdcc8df0245,c_scale,fl_relative,w_1.0,h_1.0,fl_layer_apply/cf40ce48-b28e-4086-8dee-347d6bfef60c/jordan-brooklyn-fleece-mens-shorts-nPMdCB.png",
                "https://static.nike.com/a/images/t_prod_ss/w_960,c_limit,f_auto/nfo8pqgxlpmpeftollgc/air-jordan-9-retro-og-white-black-2016-release-date.jpg",
                "https://img.ebdcdn.com/product/model/portrait/pm0488_m0.jpg?im=Resize,width=500,height=600,aspect=fill;UnsharpMask,sigma=1.0,gain=1.0&q=85"
        };
        String xRay21Description[] = {
                "Michael Jordan;• Born in Brooklyn, NY, America\n• Age: 60 (1963)\n• Height: 6′ 6″\n• Nationality: American;Michael Jeffrey Jordan, also known by his initials MJ, is an American businessman and former professional basketball player. He played fifteen seasons in the National Basketball Association from 1984 to 2003, winning six NBA championships with the Chicago Bulls.",
                "Wayne Knight;• Born in New York, NY, America\n• Age: 68 (1955)\n• Height: 5′ 7″\n• Nationality: American;Wayne Elliot Knight is an American actor. In television, he played recurring roles such as Newman on the NBC sitcom Seinfeld and Officer Don Orville on the NBC sitcom 3rd Rock from the Sun. He also voiced Igor on Toonsylvania, Mr. Blik on Catscratch and Baron Von Sheldgoose on Legend of the Three Caballeros.",

                "Basketball;$12.99;• Shell Material: composite\n• Fill Material: Butyl bladder\n• Size: 29.5\"\n\nInspired by the drive that lives inside every NBA hopeful. The Wilson NBA DRV Basketball is designed for outdoor play and built to stand up to the elements. Inflation retention lining creates longer lasting air retention with this ball designed for ultimate outdoor durability.",
                "Jordan Grey Tank;$24.99;• Brand: Jordan\n• Material: 84% polyester, 16% spandex\n\nJordan 23 Pro Dry Men's Tank provides a compressive fit with sweat-wicking stretch fabric to help keep you dry and moving freely during a workout",
                "Jordan Brooklyn Fleece;$45;• Body Material: 82% cotton, 18% polyester\n• Pocket Material: 100% cotton\n• Machine wash\n\nMake these shorts your go-to pair. Made from brushed French terry fleece, they have a relaxed fit and a secure, adjustable waistband. They're authenticated with MJ's embroidered signature.",
                "Air Jordan 9 OG;$190;• Sole Material: Rubber sole\n• Series: Air Jordan 9\n• Heel Type: Flat\n• Toe Type: Round\n\nThe Air Jordan IX arrived during MJ’s first retirement from the league. The classic design took inspiration from MJ’s global appeal with a unique outsole design and clean lines. ",
                "Morrow Glasses;$35;• Material: Acetate-metal\n• Size: 133 mm (W) x 42 mm (H)\n• Weight: 15g\n• Shape: square\n• Rim: Full-Rim\n\nLook to the future with Morrow, an urban-styled frame characterized by its true black hue and rectangle lenses. A perfect choice for the streetwise."
        };
        String xRay21Merchandise[] = {
                "",
                "",
                "target walmart costco",
                "walmart amazon target",
                "amazon target walmart",
                "amazon walmart costco",
                "target walmart amazon"
        };
        Map<String, List<String>> entry21 = new HashMap<>();
        entry21.put(KEY_TYPE, Arrays.asList(xRay21Type));
        entry21.put(KEY_NAME, Arrays.asList(xRay21Name));
        entry21.put(KEY_IMAGE, Arrays.asList(xRay21ImageUrl));
        entry21.put(KEY_DESCRIPTION, Arrays.asList(xRay21Description));
        entry21.put(KEY_MERCHANDISE, Arrays.asList(xRay21Merchandise));
        listOfItems.add(entry21);




        // Million Dollar Baby
        String xRay22Type[] = {
                "0",
                "0",
                "1",
                "1",
                "1",
                "1",
                "1"
        };
        String xRay22Name[] = {
                "Clint Eastwood",
                "Walker Scobell",

                "Pro Speed Ball",
                "Boxing Gloves",
                "Boxing Punching Mitts",
                "Jump Rope",
                "Nike Tank Top"
        };
        String xRay22ImageUrl[] = {
                "https://cdn.britannica.com/75/152975-050-99123EE6/Clint-Eastwood-2008.jpg",
                "https://encrypted-tbn1.gstatic.com/licensed-image?q=tbn:ANd9GcTLVhxwTlQ9UnDG2AvjQ-rR7Y6QlKr3hweWUmxstYQHMTMPQ9oS2rfrHyP9mvDvP4Adve-Dp6XWova_kyg",

                "https://www.modestvintageplayer.com/cdn/shop/products/pro-heritage-brown-leather-boxing-speed-ball-608859_1800x1800.jpg?v=1702649867",
                "https://superareshop.com/cdn/shop/files/vseriesgloves_red1_600x_17584a3c-8299-4f6b-971f-716def7fcde0_250x.jpg?v=1696050193",
                "https://m.media-amazon.com/images/I/81aGfZrlrNL._AC_SX679_.jpg",
                "https://m.media-amazon.com/images/I/71wm42EtoNL._AC_SX679_.jpg",
                "https://static.nike.com/a/images/t_PDP_1728_v1/f_auto,q_auto:eco/c0d08804-46b1-4a4f-a7e2-8bffeb6d70a4/zenvy-womens-light-support-padded-longline-sports-bra-cGHGFX.png"
        };
        String xRay22Description[] = {
                "Clint Eastwood;• Born in San Francisco, CA, America\n• Age: 93 (1930)\n• Height: 6′ 2″\n• Nationality: American;Clinton Eastwood Jr. (born May 31, 1930) is an American actor and film director. After achieving success in the Western TV series Rawhide, Eastwood rose to international fame with his role as the \"Man with No Name\" in Sergio Leone's Dollars Trilogy of spaghetti Westerns during the mid-1960s",
                "Hilary Swank;• Born in Lincoln, NE, America\n• Age: 49 (1974)\n• Height: 5′ 6″\n• Nationality: American;Hilary Ann Swank is an American actress and film producer. Swank first became known in 1992 for her role on the television series Camp Wilder and made her film debut with a minor role in Buffy the Vampire Slayer.",

                "Pro Leather Boxing Speed Ball;$85;• Material: 100% cow leather\n• Size: 25cm(H) x 15cm(Diameter)\n\nThe perfect addition to any gym or man-cave, quality hand-made vintage inspired Heritage Brown leather speed ball by The MVP crafted from premium full grain cowhide with a soft, supple feel and perfect rebound.\n",
                "Boxing Gloves;$89.99;• Size: Medium\n• Weight: 14 oz\n\nS4 Boxing Gloves offer everything you need to begin your boxing journey. Perfect for bagwork and drills, this glove protects your knuckles and keeps you comfortable while you hone your technique.",
                "Boxing Punching Mitts;$19.99;• Material: Faux leather, leather\n• Hand Orientation: Ambidextrous\n• Glove Type: Punching\n\nMade with high-quality synthetic leather, this punching pad offers exceptional durability and long-lasting performance.",
                "Jump Rope;$8.99;• Material: alloy steel, PVC\n• Length: 170cm\n\nThis jump rope is made of steel wires that are coated with strong PVC material, making it durable even after a long time exercising.",
                "Nike Tank Top;$65;• Body Material: 63% nylon, 37% spandex\n• Lining Material: 82% polyester, 18% spandex.\n• Pad Material: 100% polyurethane\n\nThe sewn-in 1-piece pad and longline silhouette offer enhanced coverage and shaping while sweat-wicking technology keeps you cool and comfortable."
        };
        String xRay22Merchandise[] = {
                "",
                "",
                "target costco walmart",
                "walmart amazon target",
                "amazon target walmart",
                "amazon costco walmart",
                "amazon target walmart"
        };
        Map<String, List<String>> entry22 = new HashMap<>();
        entry22.put(KEY_TYPE, Arrays.asList(xRay22Type));
        entry22.put(KEY_NAME, Arrays.asList(xRay22Name));
        entry22.put(KEY_IMAGE, Arrays.asList(xRay22ImageUrl));
        entry22.put(KEY_DESCRIPTION, Arrays.asList(xRay22Description));
        entry22.put(KEY_MERCHANDISE, Arrays.asList(xRay22Merchandise));
        listOfItems.add(entry22);




        // Death on the Nile
        String xRay23Type[] = {
                "0",
                "0",
                "1",
                "1",
                "1",
                "1",
                "1"
        };
        String xRay23Name[] = {
                "Kenneth Branagh",
                "Tom Bateman",

                "Silk Satin Bow Tie",
                "Soho Line Suit",
                "Silk Dinner Jacket",
                "Grande Unita Scarf",
                "Stretch Cotton Shirt"
        };
        String xRay23ImageUrl[] = {
                "https://image.tmdb.org/t/p/w500/AbCqqFxNi5w3nDUFdQt0DGMFh5H.jpg",
                "https://image.tmdb.org/t/p/w500/xD8dL5f5DyxchvGFi9ScKDnf45e.jpg",

                "https://www.armani.com/variants/images/17411127375856239/F/w960.jpg",
                "https://www.armani.com/variants/images/17266703523712900/F/w2500.jpg",
                "https://media.bergdorfgoodman.com/f_auto,q_auto:low,ar_5:7,c_fill,dpr_2.0,w_720/01/bg_4604529_100241_m",
                "https://media.loropiana.com/HYBRIS/FAO/FAO3766/3D7ADA38-869D-4500-839D-A2A7B57937F3/FAO3766_1000_MEDIUM.jpg",
                "https://www.prada.com/content/dam/pradabkg_products/U/UCM/UCM608/10HTF0012/UCM608_10HT_F0012_SLF.jpg"
        };
        String xRay23Description[] = {
                "Kenneth Branagh;• Born in Belfast, United Kingdom\n• Age: 63 (1960)\n• Height: 5′ 10″\n• Nationality: British, Northern Irish;Sir Kenneth Charles Branagh is a British actor and filmmaker. Born in Belfast and raised primarily in Reading, Berkshire, Branagh trained at London's Royal Academy of Dramatic Art and has served as its president since 2015.",
                "Tom Bateman;• Born in Oxford, United Kingdom\n• Age: 34 (1989)\n• Height: 6′ 2″\n• Nationality: British;Thomas Jonathan Bateman is a British actor best known for his roles as Giuliano de' Medici in the Starz historical fantasy drama series Da Vinci's Demons, as Bouc in the mystery films Murder on the Orient Express and Death on the Nile, and as Matt Pierce in the Peacock comedy thriller Based on a True Story.",

                "Silk Satin Bow Tie;$145;• Brand: Giorgio Armani\n• Material: 100% mulberry silk\n• Waterproof\n• Widely use-easy to carry with you as a backup\n\nSilk satin bow tie with adjustable interlocking fastening.",
                "Soho Line Wool and Cashmere Suit;$2995;• Brand: Giorgio Armani\n• Material: 90% virgin wool, 10% cashmere\n• Length: 73.5 cm\n\nThis Soho line sophisticated suit combines tailoring traditions with precious, refined fabrics that highlight the garment’s modern look.",
                "Silk Shantung Dinner Jacket;$5495;• Brand: Ralph Lauren\n• Material: 90% mulberry silk, 10% wool\n\nRalph Lauren Purple Label dinner jacket crafted from luxurious mulberry silk shantung. Features a lighter construction with fine canvassing and a soft shoulder for a more natural profile",
                "Grande Unita Scarf;$595;• Brand: Loro Piana\n• Material: 100% cashmere\n• Size: 175 x 43 cm\n\nClassic cashmere scarf with exclusive Loro Piana \"frisson\" finish, which gives the fabric sheen and adds luminosity to the colours. Highly versatile accessory for any occasion.",
                "Stretch Cotton Shirt;$875;• Brand: Prada\n• Material: 100% Cotton\n\nThe long-sleeved shirt made of stretch poplin has a classic collar and slim fit. Mother-of-pearl buttons add a brilliant touch to the garment."
        };
        String xRay23Merchandise[] = {
                "",
                "",
                "target costco walmart",
                "walmart amazon target",
                "amazon target walmart",
                "amazon costco walmart",
                "amazon target walmart"
        };
        Map<String, List<String>> entry23 = new HashMap<>();
        entry23.put(KEY_TYPE, Arrays.asList(xRay23Type));
        entry23.put(KEY_NAME, Arrays.asList(xRay23Name));
        entry23.put(KEY_IMAGE, Arrays.asList(xRay23ImageUrl));
        entry23.put(KEY_DESCRIPTION, Arrays.asList(xRay23Description));
        entry23.put(KEY_MERCHANDISE, Arrays.asList(xRay23Merchandise));
        listOfItems.add(entry23);




        // Pain Hustler
        String xRay24Type[] = {
                "0",
                "0",
                "1",
                "1",
                "1",
                "1",
                "1"
        };
        String xRay24Name[] = {
                "Emily Blunt",
                "Chris Evans",

                "Puff Floral Maxi Dress",
                "Name Necklace",
                "Classic Suit",
                "Classic Cotton Shirt",
                "Silk-Blend Tie"
        };
        String xRay24ImageUrl[] = {
                "https://www.onthisday.com/images/people/emily-blunt.jpg?w=360",
                "https://static.wikia.nocookie.net/fantasticfourmovies/images/5/58/ChrisEvans.jpg/revision/latest?cb=20201220010154",

                "https://www.astrthelabel.com/cdn/shop/products/ACDR101589P_GREENYELLOWFLORAL_4_1200x.jpg?v=1682102815",
                "https://cdn.myka.com/digital-asset/product/personalized-14k-gold-carrie-name-necklace-19.jpg",
                "https://www.dior.com/couture/ecommerce/media/catalog/product/q/s/1701341122_633C724A5881_C888_E01_ZHC.jpg?imwidth=1920",
                "https://www.armani.com/variants/images/17266703523712903/F/w2500.jpg",
                "https://www.armani.com/variants/images/1647597313133503/F/w960.jpg"
        };
        String xRay24Description[] = {
                "Emily Blunt;• Born in London, United Kingdom\n• Age: 40 (1983)\n• Height: 5′ 7″\n• Nationality: American, British;Emily Olivia Laura Blunt is a British actress. She is the recipient of several accolades, including a Golden Globe Award and a Screen Actors Guild Award, in addition to nominations for three British Academy Film Awards. Forbes ranked her as one of the highest-paid actresses in the world in 2020.",
                "Walker Scobell;• Born in Boston, MA, America\n• Age: 42 (1981)\n• Height: 6′ 0″\n• Nationality: American;Christopher Robert Evans is an American actor. He began his career with roles in television series such as Opposite Sex in 2000.",

                "Puff Floral Maxi Dress;$74;• Material: 100% polyester\n• Length: 58\"\n• Bust: 33\"\n• Waist: 30.7\"\n\nThis eye-catching design is perfect for a night of bold elegance, with luxurious floral print and a flattering sweetheart neckline. Make an unforgettable moment with daring sophistication!",
                "Name Necklace;$228;• Material: 14k gold\n• Length: 18\"\n\nWhen it comes to name jewelry, this classic has it all! Our Personalized 14k Gold Carrie Name Necklace looks just like the one Carrie Bradshaw wore in the hit TV show “Sex and the City.” Rendered in stunning script, it comes complete with lovely little flourishes to accentuate the first and last letters on the central name pendant.",
                "Classic Suit;$3800;• Brand: Dior\n• Material: 99% virgin wool, 1% elastane\n• 1 chest pocket and 2 side pockets\n\nTailoring is at the heart of House heritage and is the very essence of the Dior ateliers' savoir-faire. Celebrating this unique expertise, Kim Jones, Creative Director of Dior Men collections, reimagines the codes of elegance of the classic suit.",
                "Classic Cotton Shirt;$475;• Brand: Giorgio Armani\n• Material: 100% cotton\n\nMade from smooth cotton, this regular-fit shirt is available in neutral shades that can be worn everywhere. The style is defined by its French collar, back yoke and mother-of-pearl buttons.",
                "Silk-Blend Tie with Jacquard Monogram;$195;• Brand: Giorgio Armani\n• Material: 100% silk\n• Measurements: 146 x 8 cm\n\nLuxury silk blend tie featuring a contrasting jacquard chequerboard motif. A perfect accessory to customise looks on different occasions."
        };
        String xRay24Merchandise[] = {
                "",
                "",
                "target costco walmart",
                "walmart amazon target",
                "amazon target walmart",
                "amazon costco walmart",
                "amazon target walmart"
        };
        Map<String, List<String>> entry24 = new HashMap<>();
        entry24.put(KEY_TYPE, Arrays.asList(xRay24Type));
        entry24.put(KEY_NAME, Arrays.asList(xRay24Name));
        entry24.put(KEY_IMAGE, Arrays.asList(xRay24ImageUrl));
        entry24.put(KEY_DESCRIPTION, Arrays.asList(xRay24Description));
        entry24.put(KEY_MERCHANDISE, Arrays.asList(xRay24Merchandise));
        listOfItems.add(entry24);

        return listOfItems;
    }

    private static List<Movie> setUpDummyMovies(int length) {
        List<Movie> dummies = new ArrayList<>();
        String title[] = {
                "Bad Guys",
                "The Lord Of The Rings",
                "Doctor Strange III",
                "Groot's First Steps",
                "Luck"
        };

        String description = "Fusce id nisi turpis. Praesent viverra bibendum semper. "
                + "Donec tristique, orci sed semper lacinia, quam erat rhoncus massa, non congue tellus est "
                + "quis tellus. Sed mollis orci venenatis quam scelerisque accumsan. Curabitur a massa sit "
                + "amet mi accumsan mollis sed et magna. Vivamus sed aliquam risus. Nulla eget dolor in elit "
                + "facilisis mattis. Ut aliquet luctus lacus. Phasellus nec commodo erat. Praesent tempus id "
                + "lectus ac scelerisque. Maecenas pretium cursus lectus id volutpat.";
        String studio[] = {
                "Studio Zero",
                "Studio One",
                "Studio Two",
                "Studio Three",
                "Studio Four"
        };
        String category[] = {
                "Category Zero",
                "Category One",
                "Category Two",
                "Category Three",
                "Category Four"
        };
        String videoUrl[] = {
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/Demo%20Slam/Google%20Demo%20Slam_%2020ft%20Search.mp4",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/Demo%20Slam/Google%20Demo%20Slam_%2020ft%20Search.mp4",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Gmail%20Blue.mp4",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Fiber%20to%20the%20Pole.mp4",
                "http://commondatastorage.googleapis.com/android-tv/Sample%20videos/April%20Fool's%202013/Introducing%20Google%20Nose.mp4"
        };
        String bgImageUrl[] = {
                "https://thumb.canalplus.pro/http/unsafe/2560x1440/filters:quality(80)/img-hapi.canalplus.pro:80/ServiceImage/ImageID/108431902",
                "https://static1.colliderimages.com/wordpress/wp-content/uploads/2022/10/Rings-of-Power.jpg",
                "https://www.themoviedb.org/t/p/w1066_and_h600_bestv2/wcKFYIiVDvRURrzglV9kGu7fpfY.jpg",
                "https://www.themoviedb.org/t/p/w1066_and_h600_bestv2/9bColKVEAaWfmOLiLZoUhdYfevy.jpg",
                "https://www.themoviedb.org/t/p/w1066_and_h600_bestv2/3VQj6m0I6gkuRaljmhNZl0XR3by.jpg"
        };
        String cardImageUrl[] = {
                "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/81e8eaf4-42df-45fe-8eb9-f7596ba98246/df0ekt5-005cce1b-568a-4f4e-bdc3-a4879be52c8a.jpg?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOjdlMGQxODg5ODIyNjQzNzNhNWYwZDQxNWVhMGQyNmUwIiwiaXNzIjoidXJuOmFwcDo3ZTBkMTg4OTgyMjY0MzczYTVmMGQ0MTVlYTBkMjZlMCIsIm9iaiI6W1t7InBhdGgiOiJcL2ZcLzgxZThlYWY0LTQyZGYtNDVmZS04ZWI5LWY3NTk2YmE5ODI0NlwvZGYwZWt0NS0wMDVjY2UxYi01NjhhLTRmNGUtYmRjMy1hNDg3OWJlNTJjOGEuanBnIn1dXSwiYXVkIjpbInVybjpzZXJ2aWNlOmZpbGUuZG93bmxvYWQiXX0.U-5Y35kKoLSzjlhoUWLpBQOi2fZtcA9erkGynkC-VlI",
                "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEgWYFYyopTx6Ltjw_L84nqjkEMjc9_cylZgXQNq1xBHmujrjTbKog8pl_cPhotXVj1D1clWla7AImkDVATweq4AB0qk8DcgMvsaoSInPBvecwQxolSGT7lz8pgAUajuLJC2X-zr2r1z0UvKxVa-4GnLUb4WNXFjXRfuZalMo8Sn4mDefdMhGU4OjQlu/w1200-h630-p-k-no-nu/Rings%20of%20Power.jpg",
                "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/9Gtg2DzBhmYamXBS1hKAhiwbBKS.jpg",
                "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/1ifiV9ZJD4tC3tRYcnLIzH0meaB.jpg",
                "https://www.themoviedb.org/t/p/w600_and_h900_bestv2/1HOYvwGFioUFL58UVvDRG6beEDm.jpg"
        };

        int n = title.length;
        for (int index = 0; index < length; ++index) {
            dummies.add(
                    buildMovieInfo(
                            (long) index,
                            title[index % n],
                            description,
                            studio[index % n],
                            category[index % n],
                            videoUrl[index % n],
                            cardImageUrl[index % n],
                            bgImageUrl[index % n],
                            setUpDummyXRayItems(length)));
        }

        return dummies;
    }

    private static List<Map<String, List<String>>> setUpDummyXRayItems(int length) {
        List<Map<String, List<String>>> listOfItems = new ArrayList<>();

        // random x-ray for placeholders
        for (int i = 0; i < length; ++i) {
            String xRay21Type[] = {
                    "0",
                    "0",
            };
            String xRay21Name[] = {
                    "Tom Cruise",
                    "Emma Watson",
            };
            String xRay21ImageUrl[] = {
                    "https://t1.gstatic.com/licensed-image?q=tbn:ANd9GcRezb3QSPGhLptNSXoqUpKeVofpNCTLPXOG9n9o3Z2bnMp80f2AimK17SPKLa2PPkqsYkqIUAHfDgZFTs0",
                    "https://popularnetworth.com/wp-content/uploads/2021/05/6495d05033eb2029300f4a6fe5151952.jpg",
            };
            String xRay21Description[] = {
                    "",
                    ""
            };
            String xRay21Merchandise[] = {
                    "",
                    ""
            };
            Map<String, List<String>> entry = new HashMap<>();
            entry.put(KEY_TYPE, Arrays.asList(xRay21Type));
            entry.put(KEY_NAME, Arrays.asList(xRay21Name));
            entry.put(KEY_IMAGE, Arrays.asList(xRay21ImageUrl));
            entry.put(KEY_DESCRIPTION, Arrays.asList(xRay21Description));
            entry.put(KEY_MERCHANDISE, Arrays.asList(xRay21Merchandise));
            listOfItems.add(entry);
        }
        return listOfItems;
    }
}