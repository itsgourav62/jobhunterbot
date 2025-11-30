package com.jobhunter.config;

import com.jobhunter.entity.SkillOntology;
import com.jobhunter.repository.SkillOntologyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class SkillOntologyDataLoader implements CommandLineRunner {

    @Autowired
    private SkillOntologyRepository skillOntologyRepository;

    @Override
    public void run(String... args) throws Exception {
        if (skillOntologyRepository.count() == 0) {
            loadSkillOntology();
        }
    }

    private void loadSkillOntology() {
        // Programming Languages
        createSkill("Java", Arrays.asList("java", "java programming", "java8", "java 11", "java17", "openjdk", "jvm"), "Programming Language");
        createSkill("Python", Arrays.asList("python", "python3", "py", "python programming"), "Programming Language");
        createSkill("JavaScript", Arrays.asList("javascript", "js", "ecmascript", "es6", "es2015", "nodejs"), "Programming Language");
        createSkill("TypeScript", Arrays.asList("typescript", "ts", "typed javascript"), "Programming Language");
        createSkill("C#", Arrays.asList("csharp", "c sharp", ".net", "dotnet"), "Programming Language");
        createSkill("Go", Arrays.asList("golang", "go programming"), "Programming Language");

        // Web Frameworks
        createSkill("React", Arrays.asList("react", "reactjs", "react.js", "react native"), "Frontend Framework");
        createSkill("Angular", Arrays.asList("angular", "angularjs", "angular2", "typescript angular"), "Frontend Framework");
        createSkill("Vue.js", Arrays.asList("vue", "vuejs", "vue.js"), "Frontend Framework");
        createSkill("Spring Framework", Arrays.asList("spring", "spring framework", "spring boot", "springboot", "spring mvc", "spring security"), "Backend Framework");
        createSkill("Django", Arrays.asList("django", "django framework", "python django"), "Backend Framework");
        createSkill("Express.js", Arrays.asList("express", "expressjs", "express.js", "node express"), "Backend Framework");

        // Databases
        createSkill("MySQL", Arrays.asList("mysql", "mysql database", "mysql server", "mariadb"), "Database");
        createSkill("PostgreSQL", Arrays.asList("postgresql", "postgres", "psql", "pg"), "Database");
        createSkill("MongoDB", Arrays.asList("mongodb", "mongo", "nosql database", "document database"), "Database");
        createSkill("Redis", Arrays.asList("redis", "redis cache", "in-memory database"), "Database");

        // Cloud & DevOps
        createSkill("AWS", Arrays.asList("amazon web services", "amazon aws", "ec2", "s3", "lambda", "cloudformation"), "Cloud Platform");
        createSkill("Docker", Arrays.asList("docker", "containerization", "containers", "docker compose"), "DevOps");
        createSkill("Kubernetes", Arrays.asList("kubernetes", "k8s", "container orchestration", "kubectl"), "DevOps");
        createSkill("Jenkins", Arrays.asList("jenkins", "continuous integration", "ci/cd", "build automation"), "DevOps");

        // Tools & Methodologies
        createSkill("Git", Arrays.asList("git", "version control", "github", "gitlab", "bitbucket"), "Version Control");
        createSkill("REST API", Arrays.asList("rest", "rest api", "restful", "web services", "api development"), "API");
        createSkill("GraphQL", Arrays.asList("graphql", "graph ql", "query language"), "API");
        createSkill("Microservices", Arrays.asList("microservices", "microservice architecture", "service oriented architecture"), "Architecture");

        // Data & Analytics
        createSkill("Machine Learning", Arrays.asList("machine learning", "ml", "artificial intelligence", "ai", "data science"), "Data Science");
        createSkill("Apache Spark", Arrays.asList("spark", "apache spark", "big data processing"), "Big Data");

        System.out.println("✅ Skill ontology loaded with " + skillOntologyRepository.count() + " skills");
    }

    private void createSkill(String canonical, List<String> aliases, String category) {
        SkillOntology skill = new SkillOntology(canonical, aliases, category);
        skillOntologyRepository.save(skill);
    }
}