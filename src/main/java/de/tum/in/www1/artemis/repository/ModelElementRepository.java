package de.tum.in.www1.artemis.repository;

import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import de.tum.in.www1.artemis.domain.modeling.ModelElement;

/**
 * Spring Data JPA repository for the ModelElement entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ModelElementRepository extends JpaRepository<ModelElement, Long> {

    @Query("select element from ModelElement element left join fetch element.cluster cluster where element.modelElementId = :#{#modelElementId}")
    ModelElement findByModelElementIdWithCluster(@Param("modelElementId") String elementId);

    List<ModelElement> findByModelElementIdIn(List<String> elementIds);

    /**
     * Interface used to define return type for `countOtherBlocksInClusterBySubmissionId`
     */
    interface ModelElementCount {

        String getElementId();

        Long getNumberOfOtherElements();
    }

    /**
     * For the given Submission `id` returns a list of raw object array representing two columns.
     * First index/column corresponds to the TextBlock `id` while the second one corresponds to
     * the number of other blocks in the same cluster as given block with id = `id`.
     * For all TextBlock's of the Submission with the given `id`
     * finds their respective cluster and retrieves the number of other blocks in the same cluster
     * @param submissionId the id of the Submission
     * @return the number of other TextBlock's in the same cluster as the block with given `id`
     */
    @Query("""
            SELECT element.modelElementId as elementId, COUNT(DISTINCT allElements.modelElementId) as numberOfOtherElements
            FROM ModelingSubmission submission
            LEFT JOIN ModelElement element ON submission.id = element.submission.id
            LEFT JOIN ModelCluster cluster ON element.cluster.id = cluster.id
            LEFT JOIN ModelElement allElements ON cluster.id = allElements.cluster.id AND allElements.modelElementId <> element.modelElementId
            WHERE submission.id = :#{#submissionId}
            GROUP BY element.modelElementId
            """)
    List<ModelElementRepository.ModelElementCount> countOtherElementsInSameClusterForSubmissionId(@Param("submissionId") Long submissionId);

    /**
     * This function calls query `countOtherBlocksInSameClusterForSubmissionId` and converts the result into a Map
     * so that it's values will be easily accessed through key value pairs
     * @param submissionId the `id` of the Submission
     * @return a Map data type representing key value pairs where the key is the TextBlock id
     * and the value is the number of other blocks in the same cluster for that TextBlock.
     */
    default Map<String, Integer> countOtherElementsInClusterBySubmissionId(Long submissionId) {
        return countOtherElementsInSameClusterForSubmissionId(submissionId).stream()
                .collect(toMap(ModelElementRepository.ModelElementCount::getElementId, count -> count.getNumberOfOtherElements().intValue()));
    }
}
