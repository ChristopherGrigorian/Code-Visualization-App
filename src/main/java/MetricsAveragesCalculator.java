import java.util.Map;

/**
 * @author christophergrigorian
 */
public class MetricsAveragesCalculator {

    private final Map<String, ClassMetrics> classMetricsMap;

    public MetricsAveragesCalculator(Map<String, ClassMetrics> classMetricsMap) {
        this.classMetricsMap = classMetricsMap;
    }

    public double calculateAverageLOC() {
        return calculateAverage(ClassMetrics::getTotalLines);
    }

    public double calculateAverageeLOC() {
        return calculateAverage(ClassMetrics::getExecutableLines);
    }

    public double calculateAveragelLOC() {
        return calculateAverage(ClassMetrics::getLogicalLines);
    }

    public double calculateAverageAbstractness() {
        return calculateAverage(ClassMetrics::getAbstractness);
    }

    public double calculateAverageInstability() {
        return calculateAverage(ClassMetrics::getInstability);
    }

    public double calculateAverageDistance() {
        return calculateAverage(ClassMetrics::getDistance);
    }

    public double calculateAverageCyclomaticComplexity() {
        return classMetricsMap.values().stream()
                .flatMap(metrics -> metrics.getMethods().stream())
                .mapToInt(MethodMetrics::getCyclomaticComplexity)
                .average().orElse(0);
    }

    public double calculateAverageMethodsPerClass() {
        return classMetricsMap.values().stream()
                .mapToInt(metrics -> metrics.getMethods().size())
                .average().orElse(0);
    }

    public double calculateMedianLOC() {
        return calculateMedian(ClassMetrics::getTotalLines);
    }

    public double calculateStandardDeviationLOC() {
        return calculateStandardDeviation(ClassMetrics::getTotalLines);
    }

    public double calculateMaxLOC() {
        return calculateMax(ClassMetrics::getTotalLines);
    }

    public double calculateMinLOC() {
        return calculateMin(ClassMetrics::getTotalLines);
    }

    public double calculateMedianeLOC() {
        return calculateMedian(ClassMetrics::getExecutableLines);
    }

    public double calculateStandardDeviationeLOC() {
        return calculateStandardDeviation(ClassMetrics::getExecutableLines);
    }

    public double calculateMaxeLOC() {
        return calculateMax(ClassMetrics::getExecutableLines);
    }

    public double calculateMineLOC() {
        return calculateMin(ClassMetrics::getExecutableLines);
    }

    public double calculateMedianlLOC() {
        return calculateMedian(ClassMetrics::getLogicalLines);
    }

    public double calculateStandardDeviationlLOC() {
        return calculateStandardDeviation(ClassMetrics::getLogicalLines);
    }

    public double calculateMaxlLOC() {
        return calculateMax(ClassMetrics::getLogicalLines);
    }

    public double calculateMinlLOC() {
        return calculateMin(ClassMetrics::getLogicalLines);
    }

    public double calculateAverageLinesPerMethod() {
        return classMetricsMap.values().stream()
                .flatMap(metrics -> metrics.getMethods().stream())
                .mapToInt(MethodMetrics::getLinesOfCode)
                .average().orElse(0);
    }

    public double calculateAverageParametersPerMethod() {
        return classMetricsMap.values().stream()
                .flatMap(metrics -> metrics.getMethods().stream())
                .mapToInt(method -> method.getParameters().size())
                .average().orElse(0);
    }

    public double calculateCommentDensity() {
        double totalLines = classMetricsMap.values().stream()
                .mapToDouble(ClassMetrics::getTotalLines)
                .sum();
        double totalCommentLines = classMetricsMap.values().stream()
                .mapToDouble(ClassMetrics::getCommentLines)
                .sum();
        return totalLines > 0 ? totalCommentLines / totalLines : 0;
    }

    private double calculateAverage(MetricExtractor extractor) {
        return classMetricsMap.values().stream()
                .mapToDouble(extractor::extract)
                .average().orElse(0);
    }

    private double calculateMedian(MetricExtractor extractor) {
        return classMetricsMap.values().stream()
                .mapToDouble(extractor::extract)
                .sorted()
                .skip((classMetricsMap.size() - 1) / 2)
                .limit(2 - classMetricsMap.size() % 2)
                .average().orElse(0);
    }

    private double calculateStandardDeviation(MetricExtractor extractor) {
        double mean = calculateAverage(extractor);
        return Math.sqrt(classMetricsMap.values().stream()
                .mapToDouble(extractor::extract)
                .map(v -> Math.pow(v - mean, 2))
                .average().orElse(0));
    }

    private double calculateMax(MetricExtractor extractor) {
        return classMetricsMap.values().stream()
                .mapToDouble(extractor::extract)
                .max().orElse(0);
    }

    private double calculateMin(MetricExtractor extractor) {
        return classMetricsMap.values().stream()
                .mapToDouble(extractor::extract)
                .min().orElse(0);
    }

    @FunctionalInterface
    private interface MetricExtractor {
        double extract(ClassMetrics metrics);
    }
}
