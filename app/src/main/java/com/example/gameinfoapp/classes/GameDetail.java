package com.example.gameinfoapp.classes;

import java.util.List;

public class GameDetail {
    private String name;
    private String released;
    private double rating;
    private String background_image;
    private String description_raw;
    private List<Genre> genres;
    private List<Platform> platforms;

    private List<Developer> developers;
    private List<Publisher> publishers;
    private String trailer;


    public String getName() {
        return name;
    }

    public String getReleased() {
        return released;
    }

    public double getRating() {
        return rating;
    }

    public String getBackgroundImage() {
        return background_image;
    }

    public String getDescriptionRaw() {
        return description_raw;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public List<Platform> getPlatforms() {
        return platforms;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    public String getTrailer() {
        return trailer;
    }

    public List<Developer> getDevelopers() {
        return developers;
    }

    public List<Publisher> getPublishers() {
        return publishers;
    }


    // Nested Genre class
    public static class Genre {
        private String name;

        public String getName() {
            return name;
        }
    }
    public static class Publisher {
        private String name;

        public String getName() {
            return name;
        }
    }
    public static class Developer {
        private String name;

        public String getName() {
            return name;
        }
    }

    // Nested Platform class
    public static class Platform {
        private PlatformDetail platform;

        public PlatformDetail getPlatform() {
            return platform;
        }

        public static class PlatformDetail {
            private String name;

            public String getName() {
                return name;
            }
        }
    }

//    public static class Publisher {
//        private PublisherDetail publisher;
//
//        public PublisherDetail getPublisher() {
//            return publisher;
//        }
//
//        public static class PublisherDetail {
//            private String name;
//
//            public String getName() {
//                return name;
//            }
//        }
//    }
//
//    public static class Developer {
//        private DeveloperDetail developer;
//
//        public DeveloperDetail getDeveloper() {
//            return developer;
//        }
//
//        public static class DeveloperDetail {
//            private String name;
//
//            public String getName() {
//                return name;
//            }
//        }
//    }
}
