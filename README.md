# genetic-algorithm

FIXME: description

## Installation

Download from http://example.com/FIXME.

## Usage

FIXME: explanation

    $ java -jar genetic-algorithm-0.1.0-standalone.jar [args]

## Options

FIXME: listing of options this app accepts.

## Examples

An example MCVE that just favors genes that have a large sum

    (ns genetic-algorithm.mcve
      (:require [genetic-algorithm.judged-population :as jp]
                [genetic-algorithm.gene-f-helpers :as geh]
                [helpers.general-helpers :as g]
                [genetic-algorithm.fitness-function :as ff]
                [genetic-algorithm.settings :as gs]))

    (def global-rand-gen (g/new-rand-gen 99))

    ; Simple sum fitness function that just favors larger genes
    (def fit-func #(apply + %))

    (def settings
      (gs/map->Settings
        {; The chance that a gene sequence will have a random gene mutated
         :mutate-chance   0.1

         ; The chance that while combining genes the parent that genes are being taken from will switch
         ; 1 means every gene will alternate coming from one parent then the other.
         ; 0 means all the genes will come from one parent.
         :cross-chance    0.3

         ; A supplier function that supplies a random gene when called
         :gene-f          (geh/long-range-gen 0 1000 global-rand-gen)

         ; The percentage of the best gene sequences that are kept each iteration.
         ; The rest are "killed", and the population is repopulated from the remaining gene sequences
         :keep-perc       0.3

         ; A comparator that decides if low fitness scores should be favored, or if high scores should be favored.
         ; < means low scores should be favored, > means high scores should be favored
         :sort-f          >

         ; The number of gene sequences to keep in the population
         :pop-size        100

         ; How long each gene sequence is
         :sequence-length 10

         ; The fitness function that accepts a sequence of genes as an argument.
         ; Can either be a plain function, or a genetic-algorithm.fitness-function/Fitness-Function
         ; If a Fitness-Function is used, it can be indicated to be pure so it only has to be run once be gene sequence
         :fitness-f       (ff/fit-func true fit-func)}))

    (defn -main []
      (let [starting-pop (jp/population-of settings)

            evolved-pop (jp/advance-by starting-pop settings 100 global-rand-gen)]

        (println (mapv :genes evolved-pop))))


Copyright © 2018 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
