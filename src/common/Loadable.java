package common;


@FunctionalInterface
public interface Loadable<T> {

    T create(String[] metaData, int numOfClasses);
}
