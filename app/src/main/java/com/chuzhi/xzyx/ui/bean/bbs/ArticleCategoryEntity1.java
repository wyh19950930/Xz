package com.chuzhi.xzyx.ui.bean.bbs;

import java.util.List;

/**
 * @Author : wyh
 * @Time : On 2023/8/21 18:24
 * @Description : ArticleCategoryEntity1
 */
public class ArticleCategoryEntity1 {

    private List<ArtcleCategoryListDTO> artcle_category_list;

    public List<ArtcleCategoryListDTO> getArtcle_category_list() {
        return artcle_category_list;
    }

    public void setArtcle_category_list(List<ArtcleCategoryListDTO> artcle_category_list) {
        this.artcle_category_list = artcle_category_list;
    }

    public static class ArtcleCategoryListDTO {
        private int id;
        private String name;
        private int flag;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getFlag() {
            return flag;
        }

        public void setFlag(int flag) {
            this.flag = flag;
        }
    }
}
