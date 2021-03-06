package com.github.jomof.kane.arrow

import org.junit.Test
import org.apache.arrow.vector.types.pojo.Field
import org.apache.arrow.vector.types.pojo.FieldType
import org.apache.arrow.vector.types.pojo.Schema
import org.apache.arrow.memory.RootAllocator
import org.apache.arrow.memory.util.AssertionUtil

import org.apache.arrow.vector.VectorSchemaRoot
import org.apache.arrow.vector.ipc.ArrowFileWriter
import java.io.File
import java.io.FileOutputStream
import org.apache.arrow.vector.dictionary.DictionaryProvider.MapDictionaryProvider
import org.apache.arrow.vector.Float8Vector
import org.apache.arrow.vector.IntVector
import org.apache.arrow.vector.VarCharVector
import org.apache.arrow.vector.ipc.ArrowFileReader

import org.apache.arrow.vector.ipc.SeekableReadChannel
import org.apache.arrow.vector.types.FloatingPointPrecision.DOUBLE
import org.apache.arrow.vector.types.pojo.ArrowType
import org.apache.arrow.vector.types.pojo.ArrowType.FloatingPoint
import org.apache.arrow.vector.types.pojo.ArrowType.Int

import java.io.FileInputStream

class ArrowBasicsTest {
    init {
        System.setProperty("arrow.memory.debug.allocator", "0")
    }

    @Test
    fun write() {
        val intField = Field("int", FieldType(false, Int(32, true), null, null), null)
        val doubleField =
            Field("double", FieldType(false, FloatingPoint(DOUBLE), null, mapOf("admissible-type" to "dollars")), null)
        val utf8Field = Field("utf8", FieldType(false, ArrowType.Utf8.INSTANCE, null, null), null)
        val fields = listOf(intField, doubleField, utf8Field)
        val schema = Schema(fields, null)
        val sourceRootAlloc = RootAllocator()
        val root = VectorSchemaRoot.create(schema, sourceRootAlloc)
        val file = File("data/data.arrow")
        val outputStream = FileOutputStream(file)
        val provider = MapDictionaryProvider()
        val batchSize = 10000
        ArrowFileWriter(root, provider, outputStream.channel).use { writer ->
            root.rowCount = batchSize
            val doubleVector = root.getVector("double") as Float8Vector
            doubleVector.allocateNew()
            val intVector = root.getVector("int") as IntVector
            intVector.allocateNew()
            val utf8Vector = root.getVector("utf8") as VarCharVector
            utf8Vector.allocateNew()
            writer.start()
            repeat(200) { _ ->
                repeat(batchSize) {
                    doubleVector[it] = it.toDouble()
                    intVector[it] = it
                    utf8Vector.setSafe(it, "${it.toDouble() / 2.0}".toByteArray())
                }
                doubleVector.valueCount = batchSize
                intVector.valueCount = batchSize
                utf8Vector.valueCount = batchSize
                writer.writeBatch()
            }
            writer.end()
        }
    }

    @Test
    fun read() {
        val file = File("data/data.arrow")
        if (!file.exists()) {
            write()
        }
        repeat(10) {

            val fileInputStream = FileInputStream(file)
            val seekableReadChannel = SeekableReadChannel(fileInputStream.channel)
            val arrowFileReader = ArrowFileReader(
                seekableReadChannel,
                RootAllocator(Long.MAX_VALUE)
            )
            val root = arrowFileReader.vectorSchemaRoot
            val schema = root.schema
            val arrowBlocks = arrowFileReader.recordBlocks

            println(schema)
            for (arrowBlock in arrowBlocks) {
                arrowFileReader.loadRecordBatch(arrowBlock)
//                val ints = root.fieldVectors[0] as IntVector
//                val doubles = root.fieldVectors[1] as Float8Vector
//                val utf8s = root.fieldVectors[2] as VarCharVector
//            for (i in 0 until ints.valueCount) {
//                println(String(utf8s[i]))
//            }
            }
        }
    }
}