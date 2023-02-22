package mk.ukim.finki.wp.september2021.service.impl;

import mk.ukim.finki.wp.september2021.model.News;
import mk.ukim.finki.wp.september2021.model.NewsCategory;
import mk.ukim.finki.wp.september2021.model.NewsType;
import mk.ukim.finki.wp.september2021.model.exceptions.InvalidNewsCategoryIdException;
import mk.ukim.finki.wp.september2021.model.exceptions.InvalidNewsIdException;
import mk.ukim.finki.wp.september2021.repository.NewsCategoryRepository;
import mk.ukim.finki.wp.september2021.repository.NewsRepository;
import mk.ukim.finki.wp.september2021.service.NewsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsServiceImpl implements NewsService{
    private final NewsRepository newsRepository;
    private final NewsCategoryRepository newsCategoryRepository;

    public NewsServiceImpl(NewsRepository newsRepository, NewsCategoryRepository newsCategoryRepository) {
        this.newsRepository = newsRepository;
        this.newsCategoryRepository = newsCategoryRepository;
    }

    @Override
    public List<News> listAllNews() {
        return this.newsRepository.findAll();
    }

    @Override
    public News findById(Long id) {
        return this.newsRepository.findById(id).orElseThrow(() -> new InvalidNewsIdException());
    }

    @Override
    public News create(String name, String description, Double price, NewsType type, Long category) {
        NewsCategory newsCategory = this.newsCategoryRepository.findById(category)
                .orElseThrow(() -> new InvalidNewsCategoryIdException());
        News news = new News(name,description,price,type,newsCategory);
        return this.newsRepository.save(news);
    }

    @Override
    public News update(Long id, String name, String description, Double price, NewsType type, Long category) {
        News news = this.newsRepository.findById(id).orElseThrow(() -> new InvalidNewsIdException());
        NewsCategory newsCategory = this.newsCategoryRepository.findById(category)
                        .orElseThrow(() -> new InvalidNewsCategoryIdException());
        news.setName(name);
        news.setDescription(description);
        news.setPrice(price);
        news.setType(type);
        news.setCategory(newsCategory);
        return this.newsRepository.save(news);
    }

    @Override
    public News delete(Long id) {
        News news = this.newsRepository.findById(id).orElseThrow(() -> new InvalidNewsIdException());
        this.newsRepository.delete(news);
        return news;
    }

    @Override
    public News like(Long id) {
        News news = this.newsRepository.findById(id).orElseThrow(() -> new InvalidNewsIdException());
        int likes = news.getLikes();
        news.setLikes(likes+1);
        this.newsRepository.save(news);
        return news;
    }

    @Override
    public List<News> listNewsWithPriceLessThanAndType(Double price, NewsType type) {
        if(price!=null && type!=null){
            return this.newsRepository.findAllByPriceLessThanAndType(price,type);
        }else if(price!=null && type==null){
            return this.newsRepository.findAllByPriceLessThan(price);
        }else if(price==null && type!=null){
            return this.newsRepository.findAllByType(type);
        }else{
            return this.newsRepository.findAll();
        }
    }


}
