package ru.otus.dataprocessor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.otus.model.Measurement;

import java.io.InputStream;
import java.util.List;

public class ResourcesFileLoader implements Loader {

    private final String fileName;

    public ResourcesFileLoader(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public List<Measurement> load() {
        // читает файл, парсит и возвращает результат
            ObjectMapper mapper = new ObjectMapper();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {

            if(inputStream == null) {
                throw new FileProcessException("File not found: " + fileName);
            }

            return mapper.readValue(inputStream, new TypeReference<>(){});

        } catch (Exception e) {
            throw new FileProcessException("Error loading file: " + fileName, e);
        }
    }
}
