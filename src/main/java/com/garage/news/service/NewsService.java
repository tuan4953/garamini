package com.garage.news.service;

import com.garage.news.dto.NewsRequest;
import com.garage.news.model.News;
import com.garage.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;

    public List<News> getAllPublishedNews() {
        return newsRepository.findByPublishedTrueOrderByCreatedAtDesc();
    }

    public List<News> getAllNewsForAdmin() {
        return newsRepository.findAllByOrderByCreatedAtDesc();
    }

    public News getNewsById(Long id) {
        return newsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài viết có ID: " + id));
    }

    @Transactional
    public News saveOrUpdateNews(NewsRequest request) {
        News news;
        if (request.getId() != null) {
            news = getNewsById(request.getId());
            news.setTitle(request.getTitle());
            news.setSummary(request.getSummary());
            news.setContent(request.getContent());
            news.setImageUrl(request.getImageUrl());
            news.setPublished(request.getPublished());
        } else {
            news = News.builder()
                    .title(request.getTitle())
                    .summary(request.getSummary())
                    .content(request.getContent())
                    .imageUrl(request.getImageUrl())
                    .published(request.getPublished() != null ? request.getPublished() : true)
                    .build();
        }
        return newsRepository.save(news);
    }

    @Transactional
    public void togglePublishStatus(Long id) {
        News news = getNewsById(id);
        news.setPublished(!news.getPublished());
        newsRepository.save(news);
    }

    @Transactional
    public void deleteNews(Long id) {
        newsRepository.deleteById(id);
    }
}